package com.logus.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.logus.domain.Contract;
import com.logus.domain.Credor;
import com.logus.domain.Evento;
import com.logus.domain.Finalidade;
import com.logus.domain.Garantia;
import com.logus.domain.Indexador;
import com.logus.domain.InstituicaoFinanceira;
import com.logus.domain.Moeda;
import com.logus.domain.Obrigacao;
import com.logus.domain.Sistema;
import com.logus.domain.Tranche;

public class RepositoryUtil {

	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	private static DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");;

	public static Map<String, Moeda> loadCurrenciesAlreadyInserted(Connection connection) throws SQLException {
		Map<String, Moeda> currenciesDB = new HashMap<String, Moeda>();
		Statement stmt = connection.createStatement();
		String SQL = "SELECT SEQ_MOEDA, NOM_MOEDA FROM DIVIDA_PI_2022.DIV_MOEDA";
		ResultSet rs = stmt.executeQuery(SQL);
		System.out.println(SQL);

		while (rs.next()) {
			Moeda moeda = new Moeda();
			int id = rs.getInt("SEQ_MOEDA");
			String nome = rs.getString("NOM_MOEDA");
			moeda.setId(id);
			moeda.setNome(nome.trim());
			currenciesDB.put(nome, moeda);
		}
		return currenciesDB;
	}

	public static Map<String, Contract> loadContractsAlreadyInserted(Connection connection) throws SQLException {
		Map<String, Contract> contratosDB = new HashMap<String, Contract>();
		Statement stmt = connection.createStatement();
		String SQL = "SELECT A.SEQ_CONTRATO, A.NOM_CONTRATO, "
				+ "B.SEQ_TRANCHE_CONTRATO, B.NOM_TRANCHE_CONTRATO, C.SEQ_OBRIGACAO, C.NOM_OBRIGACAO "
				+ "FROM DIVIDA_PI_2022.DIV_CONTRATO A "
				+ "LEFT JOIN DIVIDA_PI_2022.DIV_TRANCHE_CONTRATO B ON A.SEQ_CONTRATO = B.SEQ_CONTRATO "
				+ "LEFT JOIN DIVIDA_PI_2022.DIV_OBRIGACAO C ON B.SEQ_TRANCHE_CONTRATO = C.SEQ_TRANCHE";
		ResultSet rs = stmt.executeQuery(SQL);
		System.out.println(SQL);

		while (rs.next()) {
			Contract contrato = new Contract();
			int id = rs.getInt("SEQ_CONTRATO");
			String nome = rs.getString("NOM_CONTRATO");
			contrato.setId(id);
			contrato.setNome(nome.trim());
			int idTranche = rs.getInt("SEQ_TRANCHE_CONTRATO");
			if (idTranche != 0) {
				Tranche tranche = new Tranche();
				String nomeTranche = rs.getString("NOM_TRANCHE_CONTRATO");
				tranche.setId(idTranche);
				tranche.setNome(nomeTranche.trim());
				contrato.setTranche(tranche);
				int idObrigacao = rs.getInt("SEQ_OBRIGACAO");
				if (idObrigacao != 0) {
					Obrigacao obrigacao = new Obrigacao();
					String nomeObrigacao = rs.getString("NOM_OBRIGACAO");
					obrigacao.setId(idObrigacao);
					obrigacao.setNome(nomeObrigacao.trim());
					tranche.getObricacoesMap().put(obrigacao.getNome(), obrigacao);
				}
			}
			contratosDB.put(nome.trim(), contrato);
		}
		return contratosDB;
	}

	public static void createContract(Contract contrato, Map<String, Moeda> currenciesAlreadyInserted,
			Map<String, Credor> creditorsAlreadyInserted, Map<String, Finalidade> finalitiesAlreadyInserted,
			Map<String, Indexador> indexersAlreadyInserted,
			Map<String, InstituicaoFinanceira> financialInstitutionsAlreadyInserted, Connection connection)
			throws SQLException {
		Statement stmt = connection.createStatement();
		Moeda currency = loadOrCreateCurrency(connection, currenciesAlreadyInserted, contrato.getNomeMoeda());
		Credor creditor = loadOrCreateCreditor(connection, creditorsAlreadyInserted, contrato.getNomeCredor());
		// TODO Enquanto as finalidades não forem informadas pelo Piauí vou colocando o
		// Tipo de Dívida {Interna ou Externa}
		Finalidade finality = loadOrCreateFinality(connection, finalitiesAlreadyInserted, contrato.getTipoDivida());
		currenciesAlreadyInserted.put(currency.getNome(), currency);
		creditorsAlreadyInserted.put(creditor.getNome(), creditor);
		finalitiesAlreadyInserted.put(finality.getNome(), finality);
		loadOrCreateIndexer(connection, indexersAlreadyInserted, contrato.getIndexadorJuros());
		InstituicaoFinanceira institution = loadOrCreateFinancialInstitution(connection,
				financialInstitutionsAlreadyInserted, contrato.getNomeCredor());
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		contrato.setInstituicaoFinanceira(institution);
		stmt.execute(contrato.dbInsert(currency, currenciesAlreadyInserted.get("REAL"), creditor, finality));
		connection.commit();
		ResultSet rs = stmt.executeQuery("SELECT seq_count FROM sequence where seq_name = 'seq_contrato'");
		if (rs.next()) {
			contrato.setId(rs.getInt("seq_count"));
		}
		rs.close();
		stmt.close();
	}

	private static InstituicaoFinanceira loadOrCreateFinancialInstitution(Connection connection,
			Map<String, InstituicaoFinanceira> financialInstitutionsAlreadyInserted, String institutionName)
			throws SQLException {
		Set<String> keySet = financialInstitutionsAlreadyInserted.keySet();
		for (String key : keySet) {
			if (key.equalsIgnoreCase(institutionName)) {
				return financialInstitutionsAlreadyInserted.get(key);
			}
		}
		InstituicaoFinanceira newInstitution = null;
		if (null != institutionName && !institutionName.isEmpty()) {
			newInstitution = createFinancialInstitution(connection, institutionName);
			financialInstitutionsAlreadyInserted.put(institutionName, newInstitution);
		}
		return newInstitution;
	}

	private static InstituicaoFinanceira createFinancialInstitution(Connection connection, String institutionName)
			throws SQLException {
		Statement stmt = connection.createStatement();
		InstituicaoFinanceira institution = new InstituicaoFinanceira();
		institution.setNome(institutionName);
		stmt.execute(institution.dbInsert());
		connection.commit();
		ResultSet rs = stmt.executeQuery("SELECT seq_count FROM sequence where seq_name = 'seq_inst_financeira'");
		if (rs.next()) {
			institution.setId(rs.getInt("seq_count"));
		}
		rs.close();
		stmt.close();
		return institution;
	}

	private static Indexador loadOrCreateIndexer(Connection connection, Map<String, Indexador> indexersAlreadyInserted,
			String indexerName) throws SQLException {
		Set<String> keySet = indexersAlreadyInserted.keySet();
		for (String key : keySet) {
			if (key.equalsIgnoreCase(indexerName)) {
				return indexersAlreadyInserted.get(key);
			}
		}
		Indexador newIndexer = null;
		if (null != indexerName && !indexerName.isEmpty()) {
			newIndexer = createIndexer(connection, indexerName);
			indexersAlreadyInserted.put(indexerName, newIndexer);
		}
		return newIndexer;
	}

	private static Indexador createIndexer(Connection connection, String indexerName) throws SQLException {
		Statement stmt = connection.createStatement();
		Indexador indexer = new Indexador();
		indexer.setNome(indexerName);
		indexer.setAcronimo(indexerName);
		stmt.execute(indexer.dbInsert());
		connection.commit();
		ResultSet rs = stmt.executeQuery("SELECT seq_count FROM sequence where seq_name = 'seq_indexer'");
		if (rs.next()) {
			indexer.setId(rs.getInt("seq_count"));
		}
		rs.close();
		stmt.close();
		return indexer;
	}

	private static Finalidade loadOrCreateFinality(Connection connection,
			Map<String, Finalidade> finalitiesAlreadyInserted, String finalityName) throws SQLException {
		Set<String> keySet = finalitiesAlreadyInserted.keySet();
		for (String key : keySet) {
			if (key.equalsIgnoreCase(finalityName)) {
				return finalitiesAlreadyInserted.get(key);
			}
		}
		Finalidade newFinality = createFinality(connection, finalityName);
		finalitiesAlreadyInserted.put(finalityName, newFinality);
		return newFinality;
	}

	private static Finalidade createFinality(Connection connection, String finalityName) throws SQLException {
		Statement stmt = connection.createStatement();
		Finalidade finality = new Finalidade();
		finality.setNome(finalityName);
		stmt.execute(finality.dbInsert());
		connection.commit();
		ResultSet rs = stmt.executeQuery("SELECT seq_count FROM sequence where seq_name = 'seq_finalidade_operacao'");
		if (rs.next()) {
			finality.setId(rs.getInt("seq_count"));
		}
		rs.close();
		stmt.close();
		return finality;
	}

	private static Credor loadOrCreateCreditor(Connection connection, Map<String, Credor> creditorsAlreadyInserted,
			String creditorName) throws SQLException {
		Set<String> keySet = creditorsAlreadyInserted.keySet();
		for (String key : keySet) {
			if (key.equalsIgnoreCase(creditorName)) {
				return creditorsAlreadyInserted.get(key);
			}
		}
		Credor newCreditor = createCreditor(connection, creditorName);
		creditorsAlreadyInserted.put(creditorName, newCreditor);
		return newCreditor;
	}

	public static Obrigacao createObrigacao(Tranche tranche, Evento evento, Connection connection,
			Contract contractInfo) throws SQLException {
		Statement stmt = connection.createStatement();
		Obrigacao obrigacao = new Obrigacao();
		obrigacao.setNome(evento.getNome());
		obrigacao.setCodigo(evento.getCodigo());
		LocalDate diaEleito = tranche.getContrato().getDiaEleito();		
		if(diaEleito==null) {
			diaEleito = LocalDate.parse(tranche.getContrato().getDataAssinatura(),format);
		}
		String dataAmortizacao = tranche.getContrato().getDataAmortizacao();
		if(null!=dataAmortizacao) {
			try {
				Date dtAmortizacao = sdf.parse(dataAmortizacao);
				String dataInicioPagamento = diaEleito.getDayOfMonth()+"/01/2021";
				Date dtInicio = sdf.parse(dataInicioPagamento);
				if(dtInicio.before(dtAmortizacao)) {
					obrigacao.setDtInicioPagamento(dataAmortizacao);
				} else {
					obrigacao.setDtInicioPagamento(dataInicioPagamento);
				}
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		stmt.execute(obrigacao.dbInsert(tranche, contractInfo));
		connection.commit();
		ResultSet rs = stmt.executeQuery("SELECT seq_count FROM sequence where seq_name = 'seq_obrigacao'");
		if (rs.next()) {
			obrigacao.setId(rs.getInt("seq_count"));
		}
		rs.close();
		stmt.close();
		return obrigacao;
	}

	public static Obrigacao createJurosDevolvidos(Tranche tranche, Connection connection, Contract contractInfo)
			throws SQLException {
		Statement stmt = connection.createStatement();
		Obrigacao obrigacao = new Obrigacao();
		obrigacao.setNome("Juros Devolvidos");
		obrigacao.setCodigo("JR-DEV");
		obrigacao.setExpIncidencia("'0'");
		LocalDate diaEleito = tranche.getContrato().getDiaEleito();		
		if(diaEleito==null) {
			LocalDate.parse(tranche.getContrato().getDataAssinatura(),format);
		}				
		obrigacao.setDtInicioPagamento(diaEleito.getDayOfMonth()+"/01/2021");
		double i = tranche.getContrato().getPercentualJuros() / 100;
		obrigacao.setExpQuitacao(
				"'SALDO_OBRIGACAO(JR-PR, VERDADEIRO) - (SALDO_OBRIGACAO(AMORT, VERDADEIRO) * (" + i + "/12))'");
		stmt.execute(obrigacao.dbInsert(tranche, contractInfo));
		connection.commit();
		ResultSet rs = stmt.executeQuery("SELECT seq_count FROM sequence where seq_name = 'seq_obrigacao'");
		if (rs.next()) {
			obrigacao.setId(rs.getInt("seq_count"));
		}
		rs.close();
		stmt.close();
		return obrigacao;
	}

	public static Obrigacao createTaxasDevolvidas(Tranche tranche, Connection connection, Contract contractInfo)
			throws SQLException {
		Statement stmt = connection.createStatement();
		Obrigacao obrigacao = new Obrigacao();
		obrigacao.setNome("Taxas Devolvidas");
		obrigacao.setCodigo("TX-DEV");
		obrigacao.setExpIncidencia("'0'");
		obrigacao.setExpQuitacao(
				"'SALDO_OBRIGACAO(''TX-CEF-PR'', VERDADEIRO) -(SALDO_OBRIGACAO(AMORT, VERDADEIRO) * (0.02/12))'");
		LocalDate diaEleito = tranche.getContrato().getDiaEleito();		
		if(diaEleito==null) {
			LocalDate.parse(tranche.getContrato().getDataAssinatura(),format);
		}				
		obrigacao.setDtInicioPagamento(diaEleito.getDayOfMonth()+"/01/2021");
		stmt.execute(obrigacao.dbInsert(tranche, contractInfo));
		connection.commit();
		ResultSet rs = stmt.executeQuery("SELECT seq_count FROM sequence where seq_name = 'seq_obrigacao'");
		if (rs.next()) {
			obrigacao.setId(rs.getInt("seq_count"));
		}
		rs.close();
		stmt.close();
		return obrigacao;
	}

	public static Obrigacao createJurosProRata(Tranche tranche, Connection connection, Contract contractInfo)
			throws SQLException {
		Statement stmt = connection.createStatement();
		Obrigacao obrigacao = new Obrigacao();
		obrigacao.setNome("Juros Pro Rata");
		obrigacao.setCodigo("JR-PR");
		LocalDate diaEleito = tranche.getContrato().getDiaEleito();		
		LocalDate dataAssinatura = LocalDate.parse(tranche.getContrato().getDataAssinatura(), format);
		String dtInicioPgto = TextUtils.padLeftZeros(String.valueOf(diaEleito.getDayOfMonth()), 2)+
				"/"+TextUtils.padLeftZeros(String.valueOf(dataAssinatura.getMonthValue()),2)+"/"+
				dataAssinatura.getYear();
		obrigacao.setDtInicioPagamento(dtInicioPgto);		
		double i = tranche.getContrato().getPercentualJuros()/100;
		obrigacao.setExpIncidencia(
				"'SE(E_DIA_ELEITO,0,(("+i+"/12) / (DIAS_PERIODO-1) ) * SALDO_OBRIGACAO(AMORT, VERDADEIRO))'");
		obrigacao.setExpQuitacao("'SALDO'");
		String prazo = getPrazo(tranche);
		obrigacao.setNumeroParcelas(prazo);
		stmt.execute(obrigacao.dbInsert(tranche, contractInfo));
		connection.commit();
		ResultSet rs = stmt.executeQuery("SELECT seq_count FROM sequence where seq_name = 'seq_obrigacao'");
		if (rs.next()) {
			obrigacao.setId(rs.getInt("seq_count"));
		}
		rs.close();
		stmt.close();
		return obrigacao;
	}

	private static String getPrazo(Tranche tranche) {
		String dataAssinatura = tranche.getContrato().getDataAssinatura();
		LocalDate dtAssinatura = LocalDate.parse(dataAssinatura, format);
		String dataTermino = tranche.getContrato().getDataTermino();
		LocalDate dtTermino = LocalDate.parse(dataTermino, format);
		long monthsBetween = ChronoUnit.MONTHS.between(dtAssinatura, dtTermino);
		return String.valueOf(monthsBetween);
	}

	public static Obrigacao createTaxaCEF(Tranche tranche, Connection connection, Contract contractInfo)
			throws SQLException {
		Statement stmt = connection.createStatement();
		Obrigacao obrigacao = new Obrigacao();
		obrigacao.setNome("Taxa CEF");
		obrigacao.setCodigo("TX-CEF");
		obrigacao.setExpIncidencia("'0'");
		obrigacao.setExpQuitacao("'SALDO_OBRIGACAO(AMORT, VERDADEIRO)*(0.02/12)'");
		LocalDate diaEleito = tranche.getContrato().getDiaEleito();		
		if(diaEleito==null) {
			LocalDate.parse(tranche.getContrato().getDataAssinatura(),format);
		}				
		obrigacao.setDtInicioPagamento(diaEleito.getDayOfMonth()+"/01/2021");
		stmt.execute(obrigacao.dbInsert(tranche, contractInfo));
		connection.commit();
		ResultSet rs = stmt.executeQuery("SELECT seq_count FROM sequence where seq_name = 'seq_obrigacao'");
		if (rs.next()) {
			obrigacao.setId(rs.getInt("seq_count"));
		}
		rs.close();
		stmt.close();
		return obrigacao;
	}

	public static Obrigacao createTaxaCEFProRata(Tranche tranche, Connection connection, Contract contractInfo)
			throws SQLException {
		Statement stmt = connection.createStatement();
		Obrigacao obrigacao = new Obrigacao();
		obrigacao.setNome("Taxa CEF Pro Rata");
		obrigacao.setCodigo("TX-CEF-PR");
		obrigacao.setExpIncidencia(
				"'SE(E_DIA_ELEITO,0,((0.02/12) / (DIAS_PERIODO-1) ) * SALDO_OBRIGACAO(AMORT, FALSO))'");
		obrigacao.setExpQuitacao("'SALDO'");
		LocalDate diaEleito = tranche.getContrato().getDiaEleito();		
		LocalDate dataAssinatura = LocalDate.parse(tranche.getContrato().getDataAssinatura(), format);
		String dtInicioPgto = TextUtils.padLeftZeros(String.valueOf(diaEleito.getDayOfMonth()), 2)+
				"/"+TextUtils.padLeftZeros(String.valueOf(dataAssinatura.getMonthValue()),2)+"/"+
				dataAssinatura.getYear();
		obrigacao.setDtInicioPagamento(dtInicioPgto);
		obrigacao.setNumeroParcelas(getPrazo(tranche));
		stmt.execute(obrigacao.dbInsert(tranche, contractInfo));
		connection.commit();
		ResultSet rs = stmt.executeQuery("SELECT seq_count FROM sequence where seq_name = 'seq_obrigacao'");
		if (rs.next()) {
			obrigacao.setId(rs.getInt("seq_count"));
		}
		rs.close();
		stmt.close();
		return obrigacao;
	}

	public static Obrigacao createTaxaDeCredito(Tranche tranche, Connection connection, Contract contractInfo)
			throws SQLException {
		Statement stmt = connection.createStatement();
		Obrigacao obrigacao = new Obrigacao();
		obrigacao.setNome("Taxa de Crédito");
		obrigacao.setCodigo("TX-CRED");
		obrigacao.setExpIncidencia("'0'");
		double i = tranche.getContrato().getPercentualTxCredito()/100;
		obrigacao.setExpQuitacao("'SALDO_OBRIGACAO(AMORT, VERDADEIRO) * ("+i+"/12)'");
		LocalDate diaEleito = tranche.getContrato().getDiaEleito();		
		if(diaEleito==null) {
			LocalDate.parse(tranche.getContrato().getDataAssinatura(),format);
		}				
		obrigacao.setDtInicioPagamento(diaEleito.getDayOfMonth()+"/01/2021");
		stmt.execute(obrigacao.dbInsert(tranche, contractInfo));
		connection.commit();
		ResultSet rs = stmt.executeQuery("SELECT seq_count FROM sequence where seq_name = 'seq_obrigacao'");
		if (rs.next()) {
			obrigacao.setId(rs.getInt("seq_count"));
		}
		rs.close();
		stmt.close();
		return obrigacao;
	}

	public static Tranche createTranche(Contract contratoCSV, Map<String, Moeda> currenciesAlreadyInserted,
			Map<String, Sistema> systemsAlreadyInserted, Map<String, Garantia> garantiasAlreadyInserted,
			Connection connection) throws SQLException {
		Statement stmt = connection.createStatement();
		Tranche tranche = null;
		if (null != contratoCSV.getTranche()) {
			tranche = new Tranche("Aditivo");
		} else {
			tranche = new Tranche("Original");
		}
		Sistema system = loadOrCreateSystem(connection, systemsAlreadyInserted, contratoCSV.getSistema());
		Garantia garantia = loadOrCreateGarantia(connection, garantiasAlreadyInserted, contratoCSV.getGarantia());
		stmt.execute(tranche.dbInsert(contratoCSV, currenciesAlreadyInserted, system, garantia));
		connection.commit();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ResultSet rs = stmt.executeQuery("SELECT seq_count FROM sequence where seq_name = 'seq_tranche_contrato'");
		if (rs.next()) {
			tranche.setId(rs.getInt("seq_count"));
		}
		rs.close();
		stmt.close();
		return tranche;
	}

	public static Moeda loadOrCreateCurrency(Connection connection, Map<String, Moeda> currenciesAlreadyInserted,
			String currencyName) throws SQLException {
		Set<String> keySet = currenciesAlreadyInserted.keySet();
		for (String key : keySet) {
			if (key.equalsIgnoreCase(currencyName)) {
				return currenciesAlreadyInserted.get(key);
			}
		}
		Moeda newCurrency = createCurrency(connection, currencyName);
		currenciesAlreadyInserted.put(currencyName, newCurrency);
		return newCurrency;
	}

	private static Moeda createCurrency(Connection connection, String currencyName) throws SQLException {
		Statement stmt = connection.createStatement();
		Moeda moeda = new Moeda(currencyName);
		stmt.execute(moeda.dbInsert());
		connection.commit();
		ResultSet rs = stmt.executeQuery("SELECT seq_count FROM sequence where seq_name = 'seq_moeda'");
		if (rs.next()) {
			moeda.setId(rs.getInt("seq_count"));
		}
		rs.close();
		stmt.close();
		return moeda;
	}

	public static Map<String, Credor> loadCreditorsAlreadyInserted(Connection connection) throws SQLException {
		Map<String, Credor> creditorsDB = new HashMap<String, Credor>();
		Statement stmt = connection.createStatement();
		String SQL = "SELECT SEQ_CREDOR, NOM_CREDOR FROM DIVIDA_PI_2022.DIV_CREDOR";
		ResultSet rs = stmt.executeQuery(SQL);
		System.out.println(SQL);

		while (rs.next()) {
			Credor creditor = new Credor();
			int id = rs.getInt("SEQ_CREDOR");
			String nome = rs.getString("NOM_CREDOR");
			creditor.setId(id);
			creditor.setNome(nome.trim());
			creditorsDB.put(nome, creditor);
		}
		return creditorsDB;
	}

	public static Map<String, Finalidade> loadFinalitiesAlreadyInserted(Connection connection) throws SQLException {
		Map<String, Finalidade> finalitiesDB = new HashMap<String, Finalidade>();
		Statement stmt = connection.createStatement();
		String SQL = "SELECT SEQ_FINALIDADE_OPERACAO, NOM_FINALIDADE FROM DIVIDA_PI_2022.DIV_FINALIDADE_OPERACAO";
		ResultSet rs = stmt.executeQuery(SQL);
		System.out.println(SQL);

		while (rs.next()) {
			Finalidade finalidade = new Finalidade();
			int id = rs.getInt("SEQ_FINALIDADE_OPERACAO");
			String nome = rs.getString("NOM_FINALIDADE");
			finalidade.setId(id);
			finalidade.setNome(nome.trim());
			finalitiesDB.put(nome, finalidade);
		}
		return finalitiesDB;
	}

	private static Credor createCreditor(Connection connection, String creditorName) throws SQLException {
		Statement stmt = connection.createStatement();
		Credor credor = new Credor(creditorName);
		stmt.execute(credor.dbInsert());
		connection.commit();
		ResultSet rs = stmt.executeQuery("SELECT seq_count FROM sequence where seq_name = 'seq_credor'");
		if (rs.next()) {
			credor.setId(rs.getInt("seq_count"));
		}
		rs.close();
		stmt.close();
		return credor;
	}

	public static Map<String, Indexador> loadIndexersAlreadyInserted(Connection connection) throws SQLException {
		Map<String, Indexador> indexersDB = new HashMap<String, Indexador>();
		Statement stmt = connection.createStatement();
		String SQL = "SELECT SEQ_INDEXADOR, NOM_INDEXADOR, ACRO_INDEXADOR FROM DIVIDA_PI_2022.DIV_INDEXADOR";
		ResultSet rs = stmt.executeQuery(SQL);
		System.out.println(SQL);

		while (rs.next()) {
			Indexador indexer = new Indexador();
			int id = rs.getInt("SEQ_INDEXADOR");
			String nome = rs.getString("NOM_INDEXADOR");
			String acronimo = rs.getString("ACRO_INDEXADOR");
			indexer.setId(id);
			indexer.setNome(nome.trim());
			indexer.setAcronimo(acronimo.trim());
			indexersDB.put(nome, indexer);
		}
		return indexersDB;
	}

	public static Map<String, Sistema> loadSystemsAlreadyInserted(Connection connection) throws SQLException {
		Map<String, Sistema> systemsDB = new HashMap<String, Sistema>();
		Statement stmt = connection.createStatement();
		String SQL = "SELECT SEQ_SISTEMA_AMORTIZACAO, NOM_SISTEMA_AMORTIZACAO, DSC_SISTEMA_AMORTIZACAO FROM DIVIDA_PI_2022.DIV_SISTEMA_AMORTIZACAO";
		ResultSet rs = stmt.executeQuery(SQL);
		System.out.println(SQL);

		while (rs.next()) {
			Sistema system = new Sistema();
			int id = rs.getInt("SEQ_SISTEMA_AMORTIZACAO");
			String nome = rs.getString("NOM_SISTEMA_AMORTIZACAO");
			String descricao = rs.getString("DSC_SISTEMA_AMORTIZACAO");
			system.setId(id);
			system.setNome(nome.trim());
			system.setDescricao(descricao.trim());
			systemsDB.put(nome, system);
		}
		return systemsDB;
	}

	public static Map<String, Garantia> loadGarantiasAlreadyInserted(Connection connection) throws SQLException {
		Map<String, Garantia> garantiasDB = new HashMap<String, Garantia>();
		Statement stmt = connection.createStatement();
		String SQL = "SELECT SEQ_GARANTIA, NOM_GARANTIA, DSC_GARANTIA FROM DIVIDA_PI_2022.DIV_GARANTIA_CONTRA_GARANTIA";
		ResultSet rs = stmt.executeQuery(SQL);
		System.out.println(SQL);

		while (rs.next()) {
			Garantia garantia = new Garantia();
			int id = rs.getInt("SEQ_GARANTIA");
			String nome = rs.getString("NOM_GARANTIA");
			String descricao = rs.getString("DSC_GARANTIA");
			garantia.setId(id);
			garantia.setNome(nome.trim());
			garantia.setDescricao(descricao.trim());
			garantiasDB.put(nome, garantia);
		}
		return garantiasDB;
	}

	public static Map<String, InstituicaoFinanceira> loadFinancialInstitutionsAlreadyInserted(Connection connection)
			throws SQLException {
		Map<String, InstituicaoFinanceira> financialInstitutionDB = new HashMap<String, InstituicaoFinanceira>();
		Statement stmt = connection.createStatement();
		String SQL = "SELECT SEQ_INST_FINANCEIRA, NOM_INST_FINANCEIRA "
				+ "FROM DIVIDA_PI_2022.DIV_INSTITUICAO_FINANCEIRA";
		ResultSet rs = stmt.executeQuery(SQL);
		System.out.println(SQL);

		while (rs.next()) {
			InstituicaoFinanceira institution = new InstituicaoFinanceira();
			int id = rs.getInt("SEQ_INST_FINANCEIRA");
			String nome = rs.getString("NOM_INST_FINANCEIRA");
			institution.setId(id);
			institution.setNome(nome.trim());
			financialInstitutionDB.put(nome, institution);
		}
		return financialInstitutionDB;
	}

	public static void updateFinalDateContract(Contract currentContract, Date finalDateContract, Connection connection)
			throws SQLException {
		Statement stmt2 = connection.createStatement();
		String DML2 = "UPDATE div_contrato " + "SET " + "    dat_final = TO_DATE('" + sdf.format(finalDateContract)
				+ "','dd/mm/yyyy') " + "WHERE " + "    seq_contrato = " + currentContract.getId();
		stmt2.executeUpdate(DML2);
		System.out.println(DML2);
		connection.commit();
	}

	public static void updateFinalDateTrancheContract(Contract currentContract, Date finalDateContract,
			Connection connection) throws SQLException {
		Statement stmt1 = connection.createStatement();
		String DML1 = "UPDATE div_tranche_contrato " + "SET " + "    dat_final = TO_DATE('"
				+ sdf.format(finalDateContract) + "','dd/mm/yyyy') " + "WHERE " + "    seq_tranche_contrato = "
				+ currentContract.getTranche().getId();
		stmt1.executeUpdate(DML1);
		System.out.println(DML1);
		connection.commit();
	}

	private static Sistema loadOrCreateSystem(Connection connection, Map<String, Sistema> systemsAlreadyInserted,
			String systemName) throws SQLException {
		Set<String> keySet = systemsAlreadyInserted.keySet();
		for (String key : keySet) {
			if (key.equalsIgnoreCase(systemName)) {
				return systemsAlreadyInserted.get(key);
			}
		}
		Sistema newSystem = null;
		if (null != systemName && !systemName.isEmpty()) {
			newSystem = createSystem(connection, systemName);
			systemsAlreadyInserted.put(systemName, newSystem);
		}
		return newSystem;
	}

	private static Sistema createSystem(Connection connection, String systemName) throws SQLException {
		Statement stmt = connection.createStatement();
		Sistema sistema = new Sistema();
		sistema.setNome(systemName);
		stmt.execute(sistema.dbInsert());
		connection.commit();
		ResultSet rs = stmt.executeQuery("SELECT seq_count FROM sequence where seq_name = 'seq_sistema_amortizacao'");
		if (rs.next()) {
			sistema.setId(rs.getInt("seq_count"));
		}
		rs.close();
		stmt.close();
		return sistema;
	}

	private static Garantia loadOrCreateGarantia(Connection connection, Map<String, Garantia> garantiasAlreadyInserted,
			String nomeGarantia) throws SQLException {
		Set<String> keySet = garantiasAlreadyInserted.keySet();
		for (String key : keySet) {
			if (key.equalsIgnoreCase(nomeGarantia)) {
				return garantiasAlreadyInserted.get(key);
			}
		}
		Garantia newGarantia = null;
		if (null != nomeGarantia && !nomeGarantia.isEmpty()) {
			newGarantia = createGarantia(connection, nomeGarantia);
			garantiasAlreadyInserted.put(nomeGarantia, newGarantia);
		}
		return newGarantia;
	}

	private static Garantia createGarantia(Connection connection, String nomeGarantia) throws SQLException {
		Statement stmt = connection.createStatement();
		Garantia garantia = new Garantia();
		garantia.setNome(nomeGarantia);
		stmt.execute(garantia.dbInsert());
		connection.commit();
		ResultSet rs = stmt.executeQuery("SELECT seq_count FROM sequence where seq_name = 'seq_garantia'");
		if (rs.next()) {
			garantia.setId(rs.getInt("seq_count"));
		}
		rs.close();
		stmt.close();
		return garantia;
	}
}