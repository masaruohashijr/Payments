package com.logus.domain;

import com.logus.domain.Evento.TipoEventoEnum;

public class Juro
  implements Evento {
  private String dataPlanilha;
  private String situacaoEvento;
  private String valorMoedaOriginal;
  private String valorRealizadoReal;
  private String valorRealizadoDolar;

  public Juro(String[] array) {
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
    return "Juros";
  }

  @Override
  public String getCodigo() {
    return "JUROS";
  }

  @Override
  public String dbInsert(int seqTranche, int seqObrigacao) {
    String insert = "";
    String tabela = "DIV_EVENTO_TRANCHE";
    String campos = "(DAT_OCORRENCIA,DAT_PREVISAO,DSC_EVENTO,SIT_EVENTO,TIP_EVENTO,VAL_EVENTO,SEQ_OBRIGACAO,SEQ_TRANCHE_CONTRATO)";
    StringBuilder values = new StringBuilder();
    values
        .append("TO_DATE('" + this.dataPlanilha + "','dd/mm/yyyy')" + ",");
    values
        .append("TO_DATE('" + this.dataPlanilha + "','dd/mm/yyyy')" + ",");
    values.append("'',");
    values.append("'" + this.situacaoEvento.toUpperCase().trim() + "',");
    values.append("'" + TipoEventoEnum.QUITACAO_OBRIGACAO + "',");
    values.append(""+this.valorMoedaOriginal+",");
    values.append(seqObrigacao + ",");
    values.append(seqTranche);
    insert = INSERT_INTO + OWNER + "." + tabela + campos + STR_VALUES + values
        + CLOSING;
    return insert;
  }
}
