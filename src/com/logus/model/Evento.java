package com.logus.model;

public interface Evento {
	String getDataPlanilha();
	void setDataPlanilha(final String dataPlanilha);
	String getSituacaoEvento();
	void setSituacaoEvento(final String situacaoEvento);
	String getValorMoedaOriginal();
	void setValorMoedaOriginal(final String valorMoedaOriginal);
	String getValorRealizadoReal();
	void setValorRealizadoReal(final String valorRealizadoReal);
	String getValorRealizadoDolar();
	void setValorRealizadoDolar(final String valorRealizadoDolar);
	String dbInsert(int seqTranche, int seqObrigacao);
	String ins = "Insert into ";
	String owner = "DIVIDA_PI_2021";
	String strValues = " values (";
	String QUITACAO_OBRIGACAO = "QUITACAO_OBRIGACAO";
	String LIBERACAO = "LIBERACAO";	
}
