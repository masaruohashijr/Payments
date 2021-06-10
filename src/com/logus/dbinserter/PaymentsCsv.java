package com.logus.dbinserter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.logus.model.Amortizacao;
import com.logus.model.Contrato;
import com.logus.model.Encargos;
import com.logus.model.Evento;
import com.logus.model.Ingresso;
import com.logus.model.Juro;
import com.logus.model.Obrigacao;
import com.logus.model.Tranche;

public class PaymentsCsv {

	private static Map<String, String> mapaDividas = new HashMap<String, String>();

	public static void main(String[] args) {
		mapaDividas.put("PROMORADIA III","PRO-MORADIA III");           
		mapaDividas.put("SANEAMENTO PARA TODOS","SANEAMENTO PARA TODOS I - SÃO PEDRO DO PI");    
		mapaDividas.put("PRO-MORADIA I"   ,"PRO-MORADIA I");
		mapaDividas.put("PROMORADIA II","PRO-MORADIA II");            
		mapaDividas.put("SAN. P/TODOS II PARNAIBA" ,"SANEAMENTO PARA TODOS II - PARNAÍBA");
		mapaDividas.put("SAN. P/ TODOS II-TERESINA","SANEAMENTO PARA TODOS II - TERESINA");
		mapaDividas.put("SANEAMENTO P/TODOS-MARCOL","SANEAMENTO PARA TODOS I - MARCOLÂNDIA");
		mapaDividas.put("SANEAMENTO P/TODOS I","SANEAMENTO PARA TODOS I - PICOS");
		mapaDividas.put("SANEAMENTO P/TODOS I-S.FC","SANEAMENTO PARA TODOS I - SÃO FRANCISCO DO PI");
		mapaDividas.put("SAN. P/TODOS I-S. JOÃO P","SANEAMENTO PARA TODOS I - SÃO JOÃO DO PI");
		mapaDividas.put("SAN. PARA TODOS I - UNIÃ","SANEAMENTO PARA TODOS I - UNIÃO");
		mapaDividas.put("SANEAMENTO PARA TODOS III","SANEAMENTO PARA TODOS III - PARNAÍBA");
		mapaDividas.put("PROTRANSPORTES - CAIXA"   ,"PROTRANSPORTE");
		mapaDividas.put("FINAC. INVEST. - FINISA I","FINANCIAMENTO À INFRAESTRUTURA E SANEAMENTO - FINISA I");
		mapaDividas.put("FINISA II","FINANCIAMENTO À INFRAESTRUTURA E SANEAMENTO");
		mapaDividas.put("PARCELAMENTO FCVS/EMGERPI","");
		mapaDividas.put("PRODESENVOLVIMENTO II","PRODESENVOLVIMENTO II");
		mapaDividas.put("ADITIVO PRODESENVOLV. II" ,"PRODESENVOLVIMENTO II");
		mapaDividas.put("PRODETUR II","PRODETUR II");
		mapaDividas.put("BNDES - PEF II","PEF II");
		mapaDividas.put("PROINVEST","PROINVEST");                
		mapaDividas.put("PARC L 11941-EMGERPI-PREV","");
		mapaDividas.put("PARC L 11941 EMGERPI-OUTR","");
		mapaDividas.put("PARC. INSS LEI 12810-EXEC","");
		mapaDividas.put("PARC INSS LEI12810-LEGISL","");
		mapaDividas.put("PARC INSS LEI 12810 MP","");   
		mapaDividas.put("INSS SIMPL ORD EDUCAÇÃO","");
		mapaDividas.put("PARCELAMENTO INSS SESAPI",""); 
		mapaDividas.put("PARC PASEP LEI 12.810/13",""); 
		mapaDividas.put("PARC EMGERPI LEI 12996 PR","");
		mapaDividas.put("PARC. EMGERPI LEI12996 TR","");
		mapaDividas.put("PARC SIMPL FGTS EMGERPI","");  
		mapaDividas.put("PARCEL. EMGERPI ITR/PERT",""); 
		mapaDividas.put("PERT ASSEMBLÉIA SENADO","");  
		mapaDividas.put("PARC. ORD/SIMPLIF. INSS E","");
		mapaDividas.put("PARCELAMENTO SIMPLIF INSS","");
		mapaDividas.put("BRB RODOVIAS","BRB - RODOVIAS");             
		mapaDividas.put("PROFISCO - BID","PROFISCO I");           
		mapaDividas.put("PROFISCO II","PROFISCO II");              
		mapaDividas.put("BIRD - PCPR II-2A.  ETAPA","PCPR-II - 2a. ETAPA");
		mapaDividas.put("DPL - BIRD","PROGRAMA DE DESENVOLVIMENTO SUSTENTÁVEL - DPL I");               
		mapaDividas.put("DPL II - BIRD","CRESCIMENTO SUSTENTÁVEL E INCLUSIVO - DPL II");            
		mapaDividas.put("SWAPP - BIRD","PILARES DE CRESCIMENTO E INCLUSÃO SOCIAL  (SWAp)");             
		mapaDividas.put("VIVA O SEMI ARIDO","PROGR. DE DESENV. SUSTENTÁVEL NO SEMI-ÁRIDO");  

		String jdbcURL = "jdbc:oracle:thin:@//192.168.0.38:1521/desenv02.logusinfo.com.br";
		String username = "DIVIDA_PI_2021";
		String password = "DIVIDA_PI_2021";

		String csvFilePath = "dvn40700.csv";

		int batchSize = 20;

		Connection connection = null;

		try {

			connection = DriverManager.getConnection(jdbcURL, username, password);
			connection.setAutoCommit(false);

			String camposObrigacao = "(SEQ_EVENTO_CONTRATO," + "DAT_OCORRENCIA," + "DAT_PREVISAO," + "DSC_EVENTO,"
					+ "SIT_EVENTO," + "TIP_EVENTO," + "VAL_EVENTO," + "SEQ_OBRIGACAO," + "SEQ_PENALIDADE,"
					+ "SEQ_TRANCHE_CONTRATO)";
			String insertObrigacao = "INSERT INTO DIVIDA_PI_2021.DIV_EVENTO_TRANCHE (" + camposObrigacao
					+ ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement stmtObrigacao = connection.prepareStatement(insertObrigacao);

			/*
			 * String sqlObrigacao = "INSERT INTO DIVIDA_PI_2021.DIV_EVENTO_TRANCHE ("
			 * +campos+") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; PreparedStatement
			 * stmtObrigacao = connection.prepareStatement(sqlObrigacao); PreparedStatement
			 * stmtLiberacao = connection.prepareStatement(stmtObrigacao);
			 */

			Map<String, Contrato> contratosBD = new HashMap<String, Contrato>();
			Statement stmt = connection.createStatement();
			String SQL = "SELECT A.SEQ_CONTRATO, A.NM_CONTRATO, "
					+ "B.SEQ_TRANCHE_CONTRATO, B.NM_TRANCHE_CONTRATO, C.SEQ_OBRIGACAO, C.NM_OBRIGACAO "
					+ "FROM DIVIDA_PI_2021.DIV_CONTRATO A " 
					+ "LEFT JOIN DIVIDA_PI_2021.DIV_TRANCHE_CONTRATO B ON A.SEQ_CONTRATO = B.SEQ_CONTRATO "
					+ "LEFT JOIN DIVIDA_PI_2021.DIV_OBRIGACAO C ON B.SEQ_TRANCHE_CONTRATO = C.SEQ_TRANCHE";
			ResultSet rs = stmt.executeQuery(SQL);
			System.out.println(SQL);

			while (rs.next()) {
				Contrato contrato = new Contrato();
				int id = rs.getInt("SEQ_CONTRATO");
				String nome = rs.getString("NM_CONTRATO");
				contrato.setId(id);
				contrato.setNome(nome.trim());
				int idTranche = rs.getInt("SEQ_TRANCHE_CONTRATO");
				if(idTranche!=0) {
					Tranche tranche = new Tranche();
					String nomeTranche = rs.getString("NM_TRANCHE_CONTRATO");
					tranche.setId(idTranche);
					tranche.setNome(nomeTranche.trim());
					contrato.setTranche(tranche);
					int idObrigacao = rs.getInt("SEQ_OBRIGACAO");
					if(idObrigacao!=0) {
						Obrigacao obrigacao = new Obrigacao();						
						String nomeObrigacao = rs.getString("NM_OBRIGACAO");
						obrigacao.setId(idObrigacao);
						obrigacao.setNome(nomeObrigacao.trim());
						tranche.getMapaObricacoes().put(obrigacao.getNome(), obrigacao);
					}					
				}
				contratosBD.put(nome.trim(), contrato);
			}

			BufferedReader lineReader = new BufferedReader(new FileReader(csvFilePath));
			String lineText = null;

			int count = 0;

			lineReader.readLine();
			Contrato contratoCSV = null;
			while ((lineText = lineReader.readLine()) != null) {
				String[] ar = lineText.split(";");
				if (!ar[0].trim().isEmpty()) {
					contratoCSV = new Contrato(ar);
					System.out.println(++count + " " + contratoCSV.toString());
					String nomeValorMapa = mapaDividas.get(contratoCSV.getNome());
					if (nomeValorMapa == null || !contratosBD.containsKey(nomeValorMapa.trim())) {
						createContrato(contratoCSV, connection);
						contratosBD.put(nomeValorMapa, contratoCSV);
					} else {
						Contrato contratoBD = contratosBD.get(nomeValorMapa.trim());
						contratoCSV.setTranche(contratoBD.getTranche());
					}
				}
				Evento evento = null;
				if (ar[19].trim().equals("Amortizacao")) {
					System.out.println("Amortizacao");
					evento = new Amortizacao(ar);
				} else if (ar[19].trim().equals("Juro")) {
					System.out.println("Juro");
					evento = new Juro(ar);
				} else if (ar[19].trim().equals("Encargos")) {
					System.out.println("Encargos");
					evento = new Encargos(ar);
				} else if (ar[19].trim().equals("Ingresso")) {
					System.out.println("Ingresso");
					evento = new Ingresso(ar);
				}
				Tranche tranche = null;
				if(null==contratoCSV.getTranche()) {
					tranche = createTranche(contratoCSV, connection);
				}	
				String insert = evento.dbInsert(1,1);
				System.out.println(insert);
				contratoCSV.add(evento);
				/*
				 * if (count % batchSize == 0) { statement.executeBatch(); }
				 */
			}
			lineReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private static Tranche createTranche(Contrato contratoCSV, Connection connection) throws SQLException {
		Statement stmt = connection.createStatement();
		Tranche tranche = new Tranche("Tranche 1");
		stmt.execute(tranche.dbInsert(contratoCSV));
		connection.commit();
		ResultSet rs = stmt.executeQuery("SELECT seq_count FROM sequence where seq_name = 'seq_tranche)contrato'");
		if (rs.next()) {
			tranche.setId(rs.getInt("seq_count"));
		}
		return tranche;
	}

	private static void createContrato(Contrato contrato, Connection connection) throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.execute(contrato.dbInsert());
		connection.commit();
		ResultSet rs = stmt.executeQuery("SELECT seq_count FROM sequence where seq_name = 'seq_contrato'");
		if (rs.next()) {
			contrato.setId(rs.getInt("seq_count"));
		}
	}
}
