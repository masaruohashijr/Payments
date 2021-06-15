package com.logus.domain;

public class InstituicaoFinanceira {
	private Integer id;
	private String nome;

	public InstituicaoFinanceira(String currencyName) {
		nome = currencyName;
	}

	public InstituicaoFinanceira() {
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
		String tabela = "DIV_INSTITUICAO_FINANCEIRA";
		String campos = "(NOM_INST_FINANCEIRA)";
		StringBuilder values = new StringBuilder();
		values.append("'"+this.nome+"'");
		insert = DBInserter.INSERT_INTO + DBInserter.OWNER + "." + tabela + campos + DBInserter.STR_VALUES + values
				+ DBInserter.CLOSING;
		return insert;
	}

}
