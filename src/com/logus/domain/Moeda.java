package com.logus.domain;

public class Moeda {
	private Integer id;
	private String nome;

	public Moeda(String currencyName) {
		nome = currencyName;
	}

	public Moeda() {
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
		String tabela = "DIV_MOEDA";
		String campos = "(nm_moeda, abr_moeda, flg_ativo, icon_moeda) ";
		StringBuilder values = new StringBuilder();
		values.append("'"+this.nome+"', "+"'"+formatAcronym()+"', 1,'$'");
		insert = DBInserter.INSERT_INTO + DBInserter.OWNER + "." + tabela + campos + DBInserter.STR_VALUES + values
				+ DBInserter.CLOSING;
		return insert;
	}
	
	private String formatAcronym() {
		if(this.nome.length() < 8) {
			return this.nome;			
		} 
		return this.nome.substring(0,8);
	}

}
