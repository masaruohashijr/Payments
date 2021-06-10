package com.logus.model;

public class Obrigacao {
	private Integer id;
	private String nome;
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
	public String dbInsert(Contrato contratoCSV) {
	    String insert = "";
	    String ins = "Insert into ";
	    String owner = "DIVIDA_PI_2021";
	    String tabela = "DIV_TRANCHE_CONTRATO";
	    String campos = "(DAT_INICIAL,NM_TRANCHE_CONTRATO,SEQ_CONTRATO)";
	    String strValues = " values (";
	    StringBuilder values = new StringBuilder();
	    values.append("TO_DATE('" + contratoCSV.getDataAssinatura() + "','dd/mm/yyyy')"
	        + ",");
	    values.append("'" + this.nome + "',");
	    values.append(contratoCSV.getId());
	    String closing = ")";
	    insert = ins + owner + "." + tabela + campos + strValues + values
	        + closing;
	    return insert;
	}	

}
