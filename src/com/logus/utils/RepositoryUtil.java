package com.logus.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.logus.domain.Contract;
import com.logus.domain.Credor;
import com.logus.domain.Evento;
import com.logus.domain.Finalidade;
import com.logus.domain.Indexador;
import com.logus.domain.InstituicaoFinanceira;
import com.logus.domain.Moeda;
import com.logus.domain.Obrigacao;
import com.logus.domain.Tranche;

public class RepositoryUtil {
	public static Map<String, Moeda> loadCurrenciesAlreadyInserted(Connection connection) throws SQLException {
		Map<String, Moeda> currenciesDB = new HashMap<String, Moeda>();
		Statement stmt = connection.createStatement();
		String SQL = "SELECT SEQ_MOEDA, NM_MOEDA FROM DIVIDA_PI_2021.DIV_MOEDA";
		ResultSet rs = stmt.executeQuery(SQL);
		System.out.println(SQL);

		while (rs.next()) {
			Moeda moeda = new Moeda();
			int id = rs.getInt("SEQ_MOEDA");
			String nome = rs.getString("NM_MOEDA");
			moeda.setId(id);
			moeda.setNome(nome.trim());
			currenciesDB.put(nome, moeda);
		}
		return currenciesDB;
	}

	public static Map<String, Contract> loadContractsAlreadyInserted(Connection connection) throws SQLException {
		Map<String, Contract> contratosDB = new HashMap<String, Contract>();
		Statement stmt = connection.createStatement();
		String SQL = "SELECT A.SEQ_CONTRATO, A.NM_CONTRATO, "
				+ "B.SEQ_TRANCHE_CONTRATO, B.NM_TRANCHE_CONTRATO, C.SEQ_OBRIGACAO, C.NM_OBRIGACAO "
				+ "FROM DIVIDA_PI_2021.DIV_CONTRATO A "
				+ "LEFT JOIN DIVIDA_PI_2021.DIV_TRANCHE_CONTRATO B ON A.SEQ_CONTRATO = B.SEQ_CONTRATO "
				+ "LEFT JOIN DIVIDA_PI_2021.DIV_OBRIGACAO C ON B.SEQ_TRANCHE_CONTRATO = C.SEQ_TRANCHE";
		ResultSet rs = stmt.executeQuery(SQL);
		System.out.println(SQL);

		while (rs.next()) {
			Contract contrato = new Contract();
			int id = rs.getInt("SEQ_CONTRATO");
			String nome = rs.getString("NM_CONTRATO");
			contrato.setId(id);
			contrato.setNome(nome.trim());
			int idTranche = rs.getInt("SEQ_TRANCHE_CONTRATO");
			if (idTranche != 0) {
				Tranche tranche = new Tranche();
				String nomeTranche = rs.getString("NM_TRANCHE_CONTRATO");
				tranche.setId(idTranche);
				tranche.setNome(nomeTranche.trim());
				contrato.setTranche(tranche);
				int idObrigacao = rs.getInt("SEQ_OBRIGACAO");
				if (idObrigacao != 0) {
					Obrigacao obrigacao = new Obrigacao();
					String nomeObrigacao = rs.getString("NM_OBRIGACAO");
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
		loadOrCreateIndexer(connection, indexersAlreadyInserted, contrato.getIndexador());
		InstituicaoFinanceira institution = loadOrCreateFinancialInstitution(connection, financialInstitutionsAlreadyInserted, contrato.getNomeCredor());
		contrato.setInstituicaoFinanceira(institution);
		stmt.execute(contrato.dbInsert(currency, creditor, finality));
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
		if (!institutionName.isEmpty()) {
			newInstitution = createFinancialInstitution(connection, institutionName);
			financialInstitutionsAlreadyInserted.put(institutionName, newInstitution);
		}
		return newInstitution;
	}

	private static InstituicaoFinanceira createFinancialInstitution(Connection connection, 
			String institutionName) throws SQLException {
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
		if (!indexerName.isEmpty()) {
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

	public static Obrigacao createObrigacao(Tranche tranche, Evento evento, Connection connection) throws SQLException {
		Statement stmt = connection.createStatement();
		Obrigacao obrigacao = new Obrigacao();
		obrigacao.setNome(evento.getNome());
		obrigacao.setCodigo(evento.getCodigo());
		stmt.execute(obrigacao.dbInsert(tranche));
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
			Connection connection) throws SQLException {
		Statement stmt = connection.createStatement();
		Tranche tranche = new Tranche("Tranche 1");
		stmt.execute(tranche.dbInsert(contratoCSV, currenciesAlreadyInserted));
		connection.commit();
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
		String SQL = "SELECT SEQ_CREDOR, NOM_TIPO_CREDOR FROM DIVIDA_PI_2021.DIV_CREDOR";
		ResultSet rs = stmt.executeQuery(SQL);
		System.out.println(SQL);

		while (rs.next()) {
			Credor creditor = new Credor();
			int id = rs.getInt("SEQ_CREDOR");
			String nome = rs.getString("NOM_TIPO_CREDOR");
			creditor.setId(id);
			creditor.setNome(nome.trim());
			creditorsDB.put(nome, creditor);
		}
		return creditorsDB;
	}

	public static Map<String, Finalidade> loadFinalitiesAlreadyInserted(Connection connection) throws SQLException {
		Map<String, Finalidade> finalitiesDB = new HashMap<String, Finalidade>();
		Statement stmt = connection.createStatement();
		String SQL = "SELECT SEQ_FINALIDADE_OPERACAO, NOM_FINALIDADE FROM DIVIDA_PI_2021.DIV_FINALIDADE_OPERACAO";
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
		String SQL = "SELECT SEQ_INDEXADOR, NOM_INDEXADOR, ACRO_INDEXADOR " + "FROM DIVIDA_PI_2021.DIV_INDEXADOR";
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

	public static Map<String, InstituicaoFinanceira> loadFinancialInstitutionsAlreadyInserted(Connection connection)
			throws SQLException {
		Map<String, InstituicaoFinanceira> financialInstitutionDB = new HashMap<String, InstituicaoFinanceira>();
		Statement stmt = connection.createStatement();
		String SQL = "SELECT SEQ_INST_FINANCEIRA, NOM_INST_FINANCEIRA "
				+ "FROM DIVIDA_PI_2021.DIV_INSTITUICAO_FINANCEIRA";
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

	public static void updateFinalDateContract(Contract currentContract, String finalDateContract, Connection connection) throws SQLException {
		Statement stmt = connection.createStatement();
		String DML = "UPDATE div_tranche_contrato "
				+ "SET "
				+ "    dat_final = TO_DATE('" + finalDateContract + "','dd/mm/yyyy') "
				+ "WHERE "
				+ "    seq_tranche_contrato = "+currentContract.getTranche().getId();
		stmt.executeUpdate(DML);
		System.out.println(DML);
	}

}
