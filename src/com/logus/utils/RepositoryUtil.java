package com.logus.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.logus.domain.Contract;
import com.logus.domain.Evento;
import com.logus.domain.Obrigacao;
import com.logus.domain.Tranche;

public class RepositoryUtil {
	public static Map<String, Contract> loadAlreadyInserted(Connection connection) throws SQLException {
		Map<String, Contract> contratosBD = new HashMap<String, Contract>();
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
			contratosBD.put(nome.trim(), contrato);
		}
		return contratosBD;
	}

	public static void createContract(Contract contrato, Connection connection) throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.execute(contrato.dbInsert());
		connection.commit();
		ResultSet rs = stmt.executeQuery("SELECT seq_count FROM sequence where seq_name = 'seq_contrato'");
		if (rs.next()) {
			contrato.setId(rs.getInt("seq_count"));
		}
		rs.close();
		stmt.close();
	}

	public static Obrigacao createObrigacao(Tranche tranche, Evento evento, Connection connection) throws SQLException {
		Statement stmt = connection.createStatement();
		Obrigacao obrigacao = new Obrigacao();
		obrigacao.setNome(evento.getNome());
		obrigacao.setCodigo(evento.getCodigoEvento());
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

	public static Tranche createTranche(Contract contratoCSV, Connection connection) throws SQLException {
		Statement stmt = connection.createStatement();
		Tranche tranche = new Tranche("Tranche 1");
		stmt.execute(tranche.dbInsert(contratoCSV));
		connection.commit();
		ResultSet rs = stmt.executeQuery("SELECT seq_count FROM sequence where seq_name = 'seq_tranche_contrato'");
		if (rs.next()) {
			tranche.setId(rs.getInt("seq_count"));
		}
		rs.close();
		stmt.close();
		return tranche;
	}

}
