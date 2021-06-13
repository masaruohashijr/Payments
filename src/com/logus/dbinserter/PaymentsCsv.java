package com.logus.dbinserter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.logus.domain.Amortizacao;
import com.logus.domain.Contract;
import com.logus.domain.Encargos;
import com.logus.domain.Evento;
import com.logus.domain.Ingresso;
import com.logus.domain.Juro;
import com.logus.domain.Obrigacao;
import com.logus.domain.Tranche;
import com.logus.utils.ConnUtil;
import com.logus.utils.ConnectionException;
import com.logus.utils.RepositoryUtil;
import com.logus.utils.FileReaderUtil;
import com.logus.utils.FromToMap;

/**
 * @author Logus
 *
 */
public class PaymentsCsv {

	public static void main(String[] args) {

		Map<String, String> mapKeys = FromToMap.init();
		Connection connection = null;
		Statement statement = null;
		BufferedReader lineReader = null;

		try {
			
			connection = ConnUtil.init();
			statement = connection.createStatement();

			Map<String, Contract> contractsAlreadyInserted = RepositoryUtil.loadAlreadyInserted(connection);

			lineReader = FileReaderUtil.get();
			lineReader.readLine();
			String lineText = null;

			int count = 0;

			Contract currentContract = null;
			
			while ((lineText = lineReader.readLine()) != null) {
				String[] ar = lineText.split(";");				
				
				// ONLY USED WHEN THE FIRST COLUMN IS FILLED IN SO IT IS NEW CONTRACT. 
				if (!ar[0].trim().isEmpty()) {
					// a new object of Contract is created.
					currentContract = new Contract(ar);
					// logging.
					System.out.println(++count + " " + currentContract.toString());
					// get the translated key.
					String translatedKey = mapKeys.get(currentContract.getNome());
					// if there were no translated key so it means that is really a new contract.
					if (translatedKey == null || !contractsAlreadyInserted.containsKey(translatedKey.trim())) {
						RepositoryUtil.createContract(currentContract, connection);
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
					System.out.println("Amortizacao");
					evento = new Amortizacao(ar);
				// Interest
				} else if (ar[19].trim().equals("Juro")) {
					System.out.println("Juro");
					evento = new Juro(ar);
					// Other Debt Charges
				} else if (ar[19].trim().equals("Encargos")) {
					System.out.println("Encargos");
					evento = new Encargos(ar);
					// New Capital Release
				} else if (ar[19].trim().equals("Ingresso")) {
					System.out.println("Ingresso");
					evento = new Ingresso(ar);
				}
				
				Tranche tranche = currentContract.getTranche();
				// If the current contract is out of Tranche (Slice)
				// a new one is created and injected.
				if (null == tranche) {
					tranche = RepositoryUtil.createTranche(currentContract, connection);
					currentContract.setTranche(tranche);
					tranche.setContrato(currentContract);
				}

				// We have to check if the Charge regarding the Financial Event 
				// is already available on the map of the Tranche (Slice).
				Obrigacao obrigacao = tranche.getObricacoesMap().get(evento.getNome());
				// If not we have to create the new Charge and put in the Map.
				if (null == obrigacao) {
					obrigacao = RepositoryUtil.createObrigacao(tranche, evento, connection);
					tranche.getObricacoesMap().put(evento.getNome(), obrigacao);
				}

				// Get the insert statement from the event.
				String insert = evento.dbInsert(tranche.getId(), obrigacao.getId());
				System.out.println(insert);
				statement.execute(insert);
				
				// Add the event to the current contract.
				currentContract.add(evento);
				// Send to database
				connection.commit();
			}
		} catch (IOException | SQLException | ConnectionException e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
				connection.close();
				lineReader.close();
			} catch (SQLException | IOException e) {
				e.printStackTrace();
			}
		}
	}
}
