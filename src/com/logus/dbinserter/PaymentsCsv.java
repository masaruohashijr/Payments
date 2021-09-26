package com.logus.dbinserter;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

import com.logus.domain.Amortizacao;
import com.logus.domain.Contract;
import com.logus.domain.Credor;
import com.logus.domain.Diferenca;
import com.logus.domain.Encargos;
import com.logus.domain.Evento;
import com.logus.domain.Finalidade;
import com.logus.domain.Garantia;
import com.logus.domain.Indexador;
import com.logus.domain.Ingresso;
import com.logus.domain.InstituicaoFinanceira;
import com.logus.domain.Juro;
import com.logus.domain.Moeda;
import com.logus.domain.Obrigacao;
import com.logus.domain.Sistema;
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

	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	/*private static String[] debts = new String[] { "CEF/dvn40700 - PROTRANSPORTES.csv",
			"CEF/dvn40700 - SANPARATODOS_PICOS.csv", "CEF/dvn40700 - PROMORADIA_I.csv",
			"CEF/dvn40700 - PROMORADIA_II.csv", "CEF/dvn40700 - PROMORADIA_III.csv",
			"CEF/dvn40700 - SANPARATODOS_MARCOLANDIA.csv", "CEF/dvn40700 - SANPARATODOS_II_PARNAIBA.csv",
			"CEF/dvn40700 - SANPARATODOS_III_PARNAIBA.csv", "CEF/dvn40700 - SANPARATODOS_II_TERESINA.csv",
			"CEF/dvn40700 - SANPARATODOS_SAO_FRANCISCO_PIAUI.csv", "CEF/dvn40700 - SANPARATODOS_SAO_JOAO_PIAUI.csv",
			"CEF/dvn40700 - SANPARATODOS_UNIAO.csv", 
			"CEF/dvn40700 - SANPARATODOS_SAO_PEDRO_PIAUI.csv", 
			"CEF/dvn40700 - FINISA_II.csv", 
			"BRB/dvn40700 - BRB_RODOVIAS.csv",
			"BRB/dvn40700 - BRB_RODOVIAS_II.csv",
			"CEF/dvn40700 - FINISA_I.csv"}; */
	private static String[] debts = new String[] { "ITAU/dvn40700 - ITAU_CAPEX.csv" };

	public static void main(String[] args) {
		Chronometer ch = new Chronometer();
		ch.start();
		Map<String, String> mapKeys = FromToMap.init();
		Map<String, Contract> mapInfo = FromToMap.initDividas();
		Connection connection = null;
		Statement statement = null;
		BufferedReader lineReader = null;

		try {

			connection = ConnUtil.init();
			statement = connection.createStatement();

			Map<String, Contract> contractsAlreadyInserted = RepositoryUtil.loadContractsAlreadyInserted(connection);
			Map<String, Moeda> currenciesAlreadyInserted = RepositoryUtil.loadCurrenciesAlreadyInserted(connection);
			Map<String, Sistema> systemsAlreadyInserted = RepositoryUtil.loadSystemsAlreadyInserted(connection);
			Map<String, Garantia> garantiasAlreadyInserted = RepositoryUtil.loadGarantiasAlreadyInserted(connection);
			Map<String, Credor> creditorsAlreadyInserted = RepositoryUtil.loadCreditorsAlreadyInserted(connection);
			Map<String, Finalidade> finalitiesAlreadyInserted = RepositoryUtil
					.loadFinalitiesAlreadyInserted(connection);
			Map<String, Indexador> indexersAlreadyInserted = RepositoryUtil.loadIndexersAlreadyInserted(connection);
			Map<String, InstituicaoFinanceira> financialInstitutionsAlreadyInserted = RepositoryUtil
					.loadFinancialInstitutionsAlreadyInserted(connection);

			for (String debt : debts) {

				lineReader = FileReaderUtil.get(debt);
				lineReader.readLine();
				String lineText = null;

				int contractCounter = 0;
				int qtdEventosRealizados = 1;
				int batchSize = 1000;
				double totalLiberacoes = 0.0;
				double totalAmortizacoes = 0.0;
				boolean registrarDiferenca = false;
				Date finalDateContract = null;

				Contract currentContract = null;

				while ((lineText = lineReader.readLine()) != null) {

					String[] ar = lineText.split(";");

					// ONLY USED WHEN THE FIRST COLUMN IS FILLED IN SO IT IS NEW CONTRACT.
					if (!ar[0].trim().isEmpty()) {
						// Update the current contract with the final date.
						if (null != currentContract) {
							if (!ar[0].contains("10030003")) {
								RepositoryUtil.updateFinalDateContract(currentContract, finalDateContract, connection);
							}
							RepositoryUtil.updateFinalDateTrancheContract(currentContract, finalDateContract,
									connection);
							finalDateContract = null;
						}
						if (!ar[0].contains("10030003")) {
							// a new object of Contract is created.
							currentContract = new Contract(ar);
							totalAmortizacoes = 0.0;
							totalLiberacoes = 0.0;
							registrarDiferenca = true;
							// logging.
							System.out.println(++contractCounter + " " + currentContract.toString());
						}
						// get the translated key.
						String translatedKey = mapKeys.get(currentContract.getNome());
						// if there were no translated key so it means that is really a new contract.
						if (null == translatedKey || translatedKey.isEmpty()
								|| !contractsAlreadyInserted.containsKey(translatedKey.trim())) {
							if (null != translatedKey && !translatedKey.isEmpty()) {
								currentContract.setNome(translatedKey);
							}
							RepositoryUtil.createContract(currentContract, currenciesAlreadyInserted,
									creditorsAlreadyInserted, finalitiesAlreadyInserted, indexersAlreadyInserted,
									financialInstitutionsAlreadyInserted, connection);
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

					// Updates the possible final date of the current contract.
					// TODO Only if it is greater than the actual finalDateContract.
					/*
					 * Date sheetDate = sdf.parse(ar[17].trim()); if (null == finalDateContract ||
					 * sheetDate.after(finalDateContract)) { finalDateContract = sheetDate; }
					 */

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
						if (evento.getSituacaoEvento().equalsIgnoreCase("Realizado")
								&& !eh2021(evento.getDataPlanilha())) {
							totalLiberacoes += Double.valueOf(evento.getValorMoedaOriginal());
						}
					}

					Tranche tranche = currentContract.getTranche();
					// If the current contract is out of Tranche (Slice)
					// a new one is created and injected.
					if (null == tranche || ar[0].contains("10030003")) {
						Contract contractInfo = extractedInfo(mapKeys, mapInfo, currentContract);
						currentContract.setDataAssinatura(ar[3]);
						currentContract.setDataTermino(contractInfo.getDataTermino());
						currentContract.setSistema(contractInfo.getSistema());
						currentContract.setGarantia(contractInfo.getGarantia());
						currentContract.setIndexadorJuros(contractInfo.getIndexadorJuros());
						currentContract.setIndexadorCorrecaoMonetaria(contractInfo.getIndexadorCorrecaoMonetaria());
						currentContract.setDiaEleito(contractInfo.getDiaEleito());
						currentContract.setPercentualJuros(contractInfo.getPercentualJuros());
						currentContract.setPercentualTxCredito(contractInfo.getPercentualTxCredito());
						currentContract.setPrazo(contractInfo.getPrazo());
						tranche = RepositoryUtil.createTranche(currentContract, currenciesAlreadyInserted,
								systemsAlreadyInserted, garantiasAlreadyInserted, connection);
						currentContract.setTranche(tranche);
						tranche.setContrato(currentContract);
					}

					// We have to check if the Charge regarding the Financial Event
					// is already available on the map of the Tranche (Slice).
					Obrigacao obrigacao = tranche.getObricacoesMap().get(evento.getNome());
					// If not we have to create the new Charge and put in the Map.
					if (null == obrigacao && !(evento instanceof Ingresso)) {
						Contract contractInfo = extractedInfo(mapKeys, mapInfo, currentContract);
						obrigacao = RepositoryUtil.createObrigacao(tranche, evento, connection, contractInfo);
						tranche.getObricacoesMap().put(evento.getNome(), obrigacao);
						if ((evento instanceof Amortizacao) && (tranche.getContrato().getSistema().equals("PRICE"))
								&& (contractInfo.getNomeCredor().equalsIgnoreCase("CAIXA")
										|| contractInfo.getNomeCredor().equalsIgnoreCase("CEF"))) {
							Obrigacao o1 = RepositoryUtil.createJurosDevolvidos(tranche, connection, contractInfo);
							tranche.getObricacoesMap().put(o1.getNome(), o1);
							Obrigacao o2 = RepositoryUtil.createJurosProRata(tranche, connection, contractInfo);
							tranche.getObricacoesMap().put(o2.getNome(), o2);
							Obrigacao o3 = RepositoryUtil.createTaxaCEF(tranche, connection, contractInfo);
							tranche.getObricacoesMap().put(o3.getNome(), o3);
							Obrigacao o4 = RepositoryUtil.createTaxaCEFProRata(tranche, connection, contractInfo);
							tranche.getObricacoesMap().put(o4.getNome(), o4);
							Obrigacao o5 = RepositoryUtil.createTaxaDeCredito(tranche, connection, contractInfo);
							tranche.getObricacoesMap().put(o5.getNome(), o5);
							Obrigacao o6 = RepositoryUtil.createTaxasDevolvidas(tranche, connection, contractInfo);
							tranche.getObricacoesMap().put(o6.getNome(), o5);
						}
					}

					// Get the insert statement from the event.
					Integer obrigacaoId = (null == obrigacao) ? 0 : obrigacao.getId();
					if (evento.getSituacaoEvento().equalsIgnoreCase("Realizado")) {
						DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
						LocalDate diaEleito = tranche.getContrato().getDiaEleito();
						if (diaEleito == null) {
							diaEleito = LocalDate.parse(tranche.getContrato().getDataAssinatura(), format);
						}
						String insert = evento.dbInsert(tranche.getId(), obrigacaoId, diaEleito);
						System.out.println(contractCounter + " " + evento.getNome() + " " + insert);
						// Add the event to the current contract.
						currentContract.add(evento);
						// Add Batch
						statement.addBatch(insert);
						qtdEventosRealizados++;
					}
					if (evento.getNome().equalsIgnoreCase("Amortização")) {
						if (registrarDiferenca && eh2021(evento.getDataPlanilha())) {
							Contract contractInfo = extractedInfo(mapKeys, mapInfo, currentContract);
							if (contractInfo.getSaldoDevedorAnoPassado() > 0) {
								double diferenca = contractInfo.getSaldoDevedorAnoPassado()
										- (totalLiberacoes - totalAmortizacoes);
								DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
								LocalDate dtEventoDiferenca = LocalDate.parse(evento.getDataPlanilha(), format);
								dtEventoDiferenca = dtEventoDiferenca.minusMonths(1);
								if(contractInfo.getSistema().equals("SAC")) {
									dtEventoDiferenca = dtEventoDiferenca.minusDays(1);
								}
								System.out.println(dtEventoDiferenca.format(format));
								Evento eventoDiferenca = new Diferenca(dtEventoDiferenca.format(format),
										String.valueOf(diferenca));
								String insertDiferenca = eventoDiferenca.dbInsert(tranche.getId(), obrigacaoId,
										tranche.getContrato().getDiaEleito());
								System.out.println(
										contractCounter + " " + eventoDiferenca.getNome() + " " + insertDiferenca);
								currentContract.add(eventoDiferenca);
								statement.addBatch(insertDiferenca);
								qtdEventosRealizados++;
							}
							totalAmortizacoes = 0;
							totalLiberacoes = 0;
							registrarDiferenca = false;
						} else {
							totalAmortizacoes += Double.valueOf(evento.getValorMoedaOriginal());
						}
					}
					if (qtdEventosRealizados % batchSize == 0) {
						// ExecuteBatch
						statement.executeBatch();
						// Send to database
						connection.commit();
					}
				}
				if (null != currentContract) {
					if (currentContract.getContCred().equals("190.491-39")) {
						finalDateContract = sdf.parse("08/03/2028");
					} else {
						finalDateContract = sdf.parse(currentContract.getDataTermino());
					}
					RepositoryUtil.updateFinalDateTrancheContract(currentContract, finalDateContract, connection);
					RepositoryUtil.updateFinalDateContract(currentContract, finalDateContract, connection);
				}
			}
			ch.stop();
		} catch (IOException | SQLException | ConnectionException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			endAndCloseAll(connection, statement, lineReader, ch);
		}
	}

	private static Contract extractedInfo(Map<String, String> mapKeys, Map<String, Contract> mapInfo,
			Contract currentContract) {
		Contract contractInfo = null;
		if (mapInfo.containsKey(currentContract.getNome())) {
			contractInfo = mapInfo.get(currentContract.getNome());
		} else if (mapKeys.containsKey(currentContract.getNome())) {
			String key = mapKeys.get(currentContract.getNome());
			contractInfo = mapInfo.get(key);
		}
		if (null == contractInfo) {
			contractInfo = currentContract;
		}
		return contractInfo;
	}

	private static void endAndCloseAll(Connection connection, Statement statement, BufferedReader lineReader,
			Chronometer ch) {
		try {
			statement.executeBatch();
			// Send to database
			connection.commit();			
			System.out.println("*****************************");
			System.out.println("*****************************");
			System.out.println("***********F I M*************");
			System.out.println("*****************************");
			System.out.println("*****************************");
			System.out.println("Tempo: " + ch.getMinutes());
			statement.close();
			connection.close();
			lineReader.close();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	private static boolean eh2021(String dataStr) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate date = LocalDate.parse(dataStr, dtf);
		if (date.getYear() == 2021) {
			return true;
		}
		return false;
	}
}
