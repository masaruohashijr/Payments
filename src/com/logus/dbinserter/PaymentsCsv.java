package com.logus.dbinserter;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import com.logus.domain.Amortizacao;
import com.logus.domain.Contract;
import com.logus.domain.Credor;
import com.logus.domain.Encargos;
import com.logus.domain.Evento;
import com.logus.domain.Finalidade;
import com.logus.domain.Indexador;
import com.logus.domain.Ingresso;
import com.logus.domain.InstituicaoFinanceira;
import com.logus.domain.Juro;
import com.logus.domain.Moeda;
import com.logus.domain.Obrigacao;
import com.logus.domain.Tranche;
import com.logus.utils.Chronometer;
import com.logus.utils.ConnUtil;
import com.logus.utils.ConnectionException;
import com.logus.utils.FileReaderUtil;
import com.logus.utils.FromToMap;
import com.logus.utils.RepositoryUtil;

/**
 * @author Logus
 *
 */
public class PaymentsCsv {

	public static void main(String[] args) {
		Chronometer ch = new Chronometer();
		ch.start();
		Map<String, String> mapKeys = FromToMap.init();
		Connection connection = null;
		Statement statement = null;
		BufferedReader lineReader = null;

		try {
			
			connection = ConnUtil.init();
			statement = connection.createStatement();

			Map<String, Contract> contractsAlreadyInserted = RepositoryUtil.loadContractsAlreadyInserted(connection);
			Map<String, Moeda> currenciesAlreadyInserted = RepositoryUtil.loadCurrenciesAlreadyInserted(connection);
			Map<String, Credor> creditorsAlreadyInserted = RepositoryUtil.loadCreditorsAlreadyInserted(connection);
			Map<String, Finalidade> finalitiesAlreadyInserted = RepositoryUtil.loadFinalitiesAlreadyInserted(connection);
			Map<String, Indexador> indexersAlreadyInserted = RepositoryUtil.loadIndexersAlreadyInserted(connection);
			Map<String, InstituicaoFinanceira> financialInstitutionsAlreadyInserted = RepositoryUtil.loadFinancialInstitutionsAlreadyInserted(connection);

			lineReader = FileReaderUtil.get();
			lineReader.readLine();
			String lineText = null;

			int contractCounter = 0;
			int lineNumber = 0;
			int batchSize = 500;
			String finalDateContract = "";

			Contract currentContract = null;
			
			while ((lineText = lineReader.readLine()) != null) {
				lineNumber++;
				String[] ar = lineText.split(";");				
				finalDateContract = ar[17].trim();
				
				// ONLY USED WHEN THE FIRST COLUMN IS FILLED IN SO IT IS NEW CONTRACT. 
				if (!ar[0].trim().isEmpty()) {
					// Update the current contract with the final date.
					if(null!=currentContract) {
						RepositoryUtil.updateFinalDateContract(currentContract, finalDateContract, connection);
					}
					// a new object of Contract is created.
					currentContract = new Contract(ar);
					// logging.
					System.out.println(++contractCounter + " " + currentContract.toString());
					// get the translated key.
					String translatedKey = mapKeys.get(currentContract.getNome());
					// if there were no translated key so it means that is really a new contract.
					if (translatedKey == null || !contractsAlreadyInserted.containsKey(translatedKey.trim())) {
						RepositoryUtil.createContract(currentContract, 
								currenciesAlreadyInserted, 
								creditorsAlreadyInserted,
								finalitiesAlreadyInserted, 
								indexersAlreadyInserted,
								financialInstitutionsAlreadyInserted,
								connection);
						contractsAlreadyInserted.put(translatedKey, currentContract);
						// There is no Tranche (Slice) to be injected here so it is empty pf Tranche.
					} else {
						// Load the Tranche (Slice) already inserted in a previous loading procedure.
						Contract contratoBD = contractsAlreadyInserted.get(translatedKey.trim());
						// Inject the dependency of the Tranche (Slice).
						currentContract.setTranche(contratoBD.getTranche());
						if (contratoBD.getTranche() != null) {
							currentContract.getTranche().setContrato(currentContract);
						}
					}
				}
				Evento evento = null;
				// Still in the same line we have to check the type of the Event.
				// Amortization
				if (ar[19].trim().equals("Amortizacao")) {
					evento = new Amortizacao(ar);
				// Interest
				} else if (ar[19].trim().equals("Juro")) {
					evento = new Juro(ar);
					// Other Debt Charges
				} else if (ar[19].trim().equals("Encargos")) {
					evento = new Encargos(ar);
					// New Capital Release
				} else if (ar[19].trim().equals("Ingresso")) {
					evento = new Ingresso(ar);
				}
				
				Tranche tranche = currentContract.getTranche();
				// If the current contract is out of Tranche (Slice)
				// a new one is created and injected.
				if (null == tranche) {
					tranche = RepositoryUtil.createTranche(
							currentContract, 
							currenciesAlreadyInserted, 
							connection);
					currentContract.setTranche(tranche);
					tranche.setContrato(currentContract);
				}

				// We have to check if the Charge regarding the Financial Event 
				// is already available on the map of the Tranche (Slice).
				Obrigacao obrigacao = tranche.getObricacoesMap().get(evento.getNome());
				// If not we have to create the new Charge and put in the Map.
				// TODO @EDNILSON if (null == obrigacao && !"Ingresso".equals(evento.getNome())) {
				if (null == obrigacao) {
					obrigacao = RepositoryUtil.createObrigacao(tranche, evento, connection);
					tranche.getObricacoesMap().put(evento.getNome(), obrigacao);
				}

				// Get the insert statement from the event.
				String insert = evento.dbInsert(tranche.getId(), obrigacao.getId());
				System.out.println(contractCounter+" "+evento.getNome()+" "+insert);
				// Add the event to the current contract.
				currentContract.add(evento);
				// Add Batch
				statement.addBatch(insert);
				if(lineNumber % batchSize == 0) {
					// ExecuteBatch
					statement.executeBatch();				
					// Send to database
					connection.commit();
				}
			}
			ch.stop();
		} catch (IOException | SQLException | ConnectionException e) {
			e.printStackTrace();
		} finally {
			endAndCloseAll(connection, statement, lineReader, ch);
		}
	}

	private static void endAndCloseAll(Connection connection, Statement statement, BufferedReader lineReader, Chronometer ch) {
		try {
			System.out.println("*****************************");
			System.out.println("*****************************");
			System.out.println("***********F I M*************");
			System.out.println("*****************************");
			System.out.println("*****************************");
			System.out.println("Tempo: "+ch.getMinutes());
			statement.close();
			connection.close();
			lineReader.close();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}
}
