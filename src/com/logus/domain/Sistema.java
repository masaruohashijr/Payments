package com.logus.domain;

public class Sistema {
	private Integer id;
	private String nome;
	private String descricao;

	public Sistema(String systemName) {
		nome = systemName;
		descricao = systemName;
	}

	public Sistema() {
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
		String tabela = "DIV_SISTEMA_AMORTIZACAO";
		String campos = "(nom_sistema_amortizacao,dsc_sistema_amortizacao)";
		StringBuilder values = new StringBuilder();
		values.append("'"+this.nome+"','"+this.descricao+"'");
		insert = DBInserter.INSERT_INTO + DBInserter.OWNER + "." + tabela + campos + DBInserter.STR_VALUES + values
				+ DBInserter.CLOSING;
		return insert;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
