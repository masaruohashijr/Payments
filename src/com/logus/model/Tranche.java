package com.logus.model;

import java.util.HashMap;
import java.util.Map;

public class Tranche {
	private Integer id;
	private String nome;
	private Contrato contrato;
	private Map<String, Obrigacao> mapaObricacoes = new HashMap<String, Obrigacao>();
	public Tranche(String nome) {
		super();
		this.nome = nome;
	}
	public Tranche() {
		// TODO Auto-generated constructor stub
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
	public Map<String, Obrigacao> getMapaObricacoes() {
		return mapaObricacoes;
	}
	public void setMapaObricacoes(Map<String, Obrigacao> mapaObricacoes) {
		this.mapaObricacoes = mapaObricacoes;
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
  /**
   * @return {@link #contrato}
   */
  public Contrato getContrato() {
    return contrato;
  }
  /**
   * @param contrato atualiza {@link #contrato}.
   */
  public void setContrato(Contrato contrato) {
    this.contrato = contrato;
  }	
}
