package com.logus.domain;

public class Garantia {
	private Integer id;
	private String nome;
	private String descricao;

	public Garantia(String nomeGarantia) {
		nome = nomeGarantia;
		descricao = nomeGarantia;
	}

	public Garantia() {
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
		String tabela = "DIV_GARANTIA_CONTRA_GARANTIA";
		String campos = "(nom_garantia,dsc_garantia)";
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
