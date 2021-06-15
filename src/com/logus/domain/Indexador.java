package com.logus.domain;

public class Indexador {
	private Integer id;
	private String nome;
	private String acronimo;

	public Indexador(String currencyName) {
		nome = currencyName;
	}

	public Indexador() {
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
		String tabela = "DIV_INDEXADOR";
		String campos = "(nom_indexador,acro_indexador,tipo_medicao,flg_ativo)";
		StringBuilder values = new StringBuilder();
		values.append("'"+this.nome+"','"+this.acronimo+"','INDICE_MES',1");
		insert = DBInserter.INSERT_INTO + DBInserter.OWNER + "." + tabela + campos + DBInserter.STR_VALUES + values
				+ DBInserter.CLOSING;
		return insert;
	}

	public String getAcronimo() {
		return acronimo;
	}

	public void setAcronimo(String acronimo) {
		this.acronimo = acronimo;
	}

}
