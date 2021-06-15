package com.logus.domain;

public class Finalidade {
	private Integer id;
	private String nome;

	public Finalidade(String currencyName) {
		nome = currencyName;
	}

	public Finalidade() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String dbInsert() {
		String insert = "";
		String tabela = "DIV_FINALIDADE_OPERACAO";
		String campos = "(nom_finalidade,seq_tip_operacao,flg_ativo)";
		StringBuilder values = new StringBuilder();
		values.append("'"+this.nome+"',1, 1");
		insert = DBInserter.INSERT_INTO + DBInserter.OWNER + "." + tabela + campos + DBInserter.STR_VALUES + values
				+ DBInserter.CLOSING;
		return insert;
	}

}
