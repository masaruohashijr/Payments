package com.logus.domain;

import java.util.HashMap;
import java.util.Map;

import com.logus.utils.RepositoryUtil;

public class Tranche {
	private Integer id;
	private String nome;
	private Contract contrato;
	private Map<String, Obrigacao> obricacoesMap = new HashMap<String, Obrigacao>();
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
	public Map<String, Obrigacao> getObricacoesMap() {
		return obricacoesMap;
	}
	public void setMapaObricacoes(Map<String, Obrigacao> mapaObricacoes) {
		this.obricacoesMap = mapaObricacoes;
	}
	public String dbInsert(Contract contratoCSV, Map<String, Moeda> currenciesAlreadyInserted, Sistema system, Garantia garantia) {
		String nomeMoeda = contratoCSV.getNomeMoeda();
		Moeda moeda = currenciesAlreadyInserted.get(nomeMoeda);
	    String insert = "";
	    String tabela = "DIV_TRANCHE_CONTRATO";
	    String campos = "(DAT_INICIAL,"
	    		+ "NOM_TRANCHE_CONTRATO,"
	    		+ "SEQ_CONTRATO, "
	    		+ "SEQ_MOEDA_CONTRATADA, "
	    		+ "SEQ_MOEDA_LOCAL, "
	    		+ "SEQ_SISTEMA_AMORT, "
	    		+ "SEQ_GARANTIA, "
	    		+ "NUM_CARENCIA_MESES, "
	    		+ "DSC_JUROS, "
	    		+ "DSC_CORRECAO, "
	    		+ "SEQ_INST_FINANCEIRA "
	    		+ ")";
	    StringBuilder values = new StringBuilder();
	    values.append("TO_DATE('" + contratoCSV.getDataAssinatura() + "','dd/mm/yyyy')"
	        + ",");
	    values.append("'" + this.nome + "',");
	    values.append(contratoCSV.getId() + ",");
		values.append(moeda.getId() + ",");
	    values.append(currenciesAlreadyInserted.get("REAL").getId() + ",");
	    values.append(system.getId() + ",");
	    values.append(garantia.getId() + ",");
	    values.append(contratoCSV.getCarenciaMeses() + ",");
	    values.append("'" + contratoCSV.getIndexadorJuros() + "',");
	    values.append("'" + contratoCSV.getIndexadorCorrecaoMonetaria() + "',");
	    values.append(contratoCSV.getInstitution().getId());
	    insert = DBInserter.INSERT_INTO + DBInserter.OWNER + "." + tabela + campos + DBInserter.STR_VALUES + values
	        + DBInserter.CLOSING;
	    return insert;
	}
  /**
   * @return {@link #contrato}
   */
  public Contract getContrato() {
    return contrato;
  }
  /**
   * @param contrato atualiza {@link #contrato}.
   */
  public void setContrato(Contract contrato) {
    this.contrato = contrato;
  }	
}
