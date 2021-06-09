package com.logus.model;

public class Ingresso implements Evento {
	private String dataPlanilha;
	private String situacaoEvento	;
	private String valorMoedaOriginal;
	private String valorRealizadoReal;
	private String valorRealizadoDolar;
	public Ingresso(String[] array) {
		super();
		this.dataPlanilha = array[17];
		this.situacaoEvento = array[18];
		this.valorMoedaOriginal = array[20];
		this.valorRealizadoReal = array[21];
		this.valorRealizadoDolar = array[22];
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
	public String dbInsert(int tranche, int seqObrigacao, int sequence) {
		String insert = "";
		String ins = "Insert into ";
		String owner = "DIVIDA_PI_2021";
		String tabela = "DIV_LIBERACAO";
		String campos = "(SEQ_EVENTO_CONTRATO,DAT_OCORRENCIA,DAT_PREVISAO,DSC_EVENTO,SIT_EVENTO,TIP_EVENTO,VAL_EVENTO,SEQ_OBRIGACAO,SEQ_PENALIDADE,SEQ_TRANCHE_CONTRATO)";
		String strValues = " values (";
		StringBuilder values = new StringBuilder();
		values.append(sequence+",");
		values.append(this.dataPlanilha+",");
		values.append(this.dataPlanilha+",");
		values.append("'',");
		values.append(this.situacaoEvento+",");
		values.append(QUITACAO_OBRIGACAO+",");
		values.append(this.valorMoedaOriginal+",");
		values.append("'',");
		values.append("'',");
		values.append(tranche);
		String closing = ");";
		insert = ins + owner +"."+ tabela + campos + strValues + sequence + values + closing;
		return insert;
	}
}
