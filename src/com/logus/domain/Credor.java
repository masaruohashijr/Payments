package com.logus.domain;

public class Credor {
	private Integer id;
	private String nome;

	public Credor(String currencyName) {
		nome = currencyName;
	}

	public Credor() {
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
		String tabela = "DIV_CREDOR";
		String campos = "(nm_credor,acronimo_credor,dsc_credor,seq_tipo_credor,flg_ativo)";
		StringBuilder values = new StringBuilder();
		String formattedAcronym = formatAcronym();
		values.append("'"+this.nome+"', "+"'"+formattedAcronym+"',"+"'"+getNome()+"',1, 1");
		insert = DBInserter.INSERT_INTO + DBInserter.OWNER + "." + tabela + campos + DBInserter.STR_VALUES + values
				+ DBInserter.CLOSING;
		return insert;
	}

	private String formatAcronym() {
		if(this.nome.length() < 10) {
			return this.nome;			
		} 
		return this.nome.substring(0,10);
	}

}
