package com.logus.domain;

import java.time.LocalDate;

public class Ingresso implements Evento {
	private String dataPlanilha;
	private String situacaoEvento	;
	private String valorMoedaOriginal;
	private String valorRealizadoReal;
	private String valorRealizadoDolar;
	public Ingresso(String[] array) {
		super();
		this.dataPlanilha = array[17].trim();
		this.situacaoEvento = array[18].trim();
		this.valorMoedaOriginal = array[20].trim();
		this.valorMoedaOriginal = this.valorMoedaOriginal.replace(".", "").replace(",", ".");
		this.valorRealizadoReal = array[21].trim();
		this.valorRealizadoDolar = array[22].trim();
	}	
	public String getDataPlanilha() {
		return dataPlanilha;
	}
	public void setDataPlanilha(String dataPlanilha) {
		this.dataPlanilha = dataPlanilha;
	}
	public String getSituacaoEvento() {
		return situacaoEvento;
	}
	public void setSituacaoEvento(String situacaoEvento) {
		this.situacaoEvento = situacaoEvento;
	}
	public String getValorMoedaOriginal() {
		return valorMoedaOriginal;
	}
	public void setValorMoedaOriginal(String valorMoedaOriginal) {
		this.valorMoedaOriginal = valorMoedaOriginal;
	}
	public String getValorRealizadoReal() {
		return valorRealizadoReal;
	}
	public void setValorRealizadoReal(String valorRealizadoReal) {
		this.valorRealizadoReal = valorRealizadoReal;
	}
	public String getValorRealizadoDolar() {
		return valorRealizadoDolar;
	}
	public void setValorRealizadoDolar(String valorRealizadoDolar) {
		this.valorRealizadoDolar = valorRealizadoDolar;
	} 
	
	@Override
    public String getNome() {
      return "Ingresso";
    }
	
	@Override
	public String getCodigo() {
	  return "INGRESSO";
	}
	
	public String dbInsert(int seqTranche, int seqObrigacao, LocalDate diaEleito) {
		String insert = "";
		String tabela = "DIV_LIBERACAO";
		String campos = "(DAT_OCORRENCIA,DAT_PREVISAO,"
		    + "DSC_EVENTO,SIT_EVENTO,TIP_EVENTO,VAL_EVENTO,SEQ_TRANCHE_CONTRATO,NOM_LIBERACAO)";
        StringBuilder values = new StringBuilder();
        values.append("TO_DATE('" + this.dataPlanilha + "','dd/mm/yyyy')"
            + ",");
        values.append("TO_DATE('" + this.dataPlanilha + "','dd/mm/yyyy')"
            + ",");
        values.append("'',");
        values.append("'"+this.situacaoEvento.toUpperCase().trim()+"',");
        values.append("'"+TipoEventoEnum.LIBERACAO+"',");
        values.append(this.valorMoedaOriginal+",");
        values.append(seqTranche+",");
        values.append("'Liberação'");
		insert = INSERT_INTO + OWNER +"."+ tabela + campos + STR_VALUES + values + CLOSING;
		return insert;
	}
}
