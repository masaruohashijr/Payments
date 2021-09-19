package com.logus.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.logus.utils.TextUtils;

public class Amortizacao implements Evento {
	private String dataPlanilha;
	private String valorMoedaOriginal;
	private String valorRealizadoReal;
	private String valorRealizadoDolar;
	private String situacaoEvento;

	public Amortizacao(String[] array) {
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

	public void setDataPlanilha(final String dataPlanilha) {
		this.dataPlanilha = dataPlanilha;
	}

	public String getValorMoedaOriginal() {
		return valorMoedaOriginal;
	}

	public void setValorMoedaOriginal(final String valorMoedaOriginal) {
		this.valorMoedaOriginal = valorMoedaOriginal;
	}

	public String getValorRealizadoReal() {
		return valorRealizadoReal;
	}

	public void setValorRealizadoReal(final String valorRealizadoReal) {
		this.valorRealizadoReal = valorRealizadoReal;
	}

	public String getValorRealizadoDolar() {
		return valorRealizadoDolar;
	}

	public void setValorRealizadoDolar(final String valorRealizadoDolar) {
		this.valorRealizadoDolar = valorRealizadoDolar;
	}

	@Override
	public String getSituacaoEvento() {
		// TODO Auto-generated method stub
		return this.situacaoEvento;
	}

	@Override
	public void setSituacaoEvento(String situacaoEvento) {
		// TODO Auto-generated method stub
		this.situacaoEvento = situacaoEvento;
	}

	@Override
	public String getNome() {
		return "Amortização";
	}

	@Override
	public String getCodigo() {
		return "AMORT";
	}

	public String dbInsert(int seqTranche, int seqObrigacao, LocalDate diaEleito) {
		String insert = "";
		String tabela = "DIV_EVENTO_TRANCHE";
		String campos = "(DAT_OCORRENCIA,DAT_PREVISAO,DSC_EVENTO,SIT_EVENTO,TIP_EVENTO,VAL_EVENTO,SEQ_OBRIGACAO,SEQ_TRANCHE_CONTRATO)";
		StringBuilder values = new StringBuilder();		
		values.append("TO_DATE('" + this.dataPlanilha + "','dd/mm/yyyy')" + ",");
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate dataPrevisao = TextUtils.getDataPrevisao(String.valueOf(diaEleito.getDayOfMonth()), this.dataPlanilha, format);
		values.append("TO_DATE('" + dataPrevisao.format(format) + "','dd/mm/yyyy')" + ",");
		values.append("'',");
		values.append("'" + this.situacaoEvento.toUpperCase().trim() + "',");
		values.append("'" + TipoEventoEnum.QUITACAO_OBRIGACAO + "',");
		values.append("" + this.valorMoedaOriginal + ",");
		values.append(seqObrigacao + ",");
		values.append(seqTranche);
		insert = INSERT_INTO + OWNER + "." + tabela + campos + STR_VALUES + values + CLOSING;
		return insert;
	}

}

