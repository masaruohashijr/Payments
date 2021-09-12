package com.logus.domain;

public class Diferenca
  implements Evento {
  private String dataPlanilha;
  private String valorMoedaOriginal;
  private String valorRealizadoReal;
  private String valorRealizadoDolar;
  private String situacaoEvento;

  public Diferenca(String dataPlanilha, String valorDiferenca) {
    super();
    this.dataPlanilha = dataPlanilha;
    this.situacaoEvento = "Realizado";
    this.valorMoedaOriginal = valorDiferenca;
    this.valorRealizadoReal = valorDiferenca;
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
    return "Diferença";
  }

  @Override
  public String getCodigo() {
    return "DIFERENCA";
  }

  public String dbInsert(int seqTranche, int seqObrigacao) {
    String insert = "";
    String tabela = "DIV_EVENTO_TRANCHE";
    String campos = "(DAT_OCORRENCIA,DAT_PREVISAO,DSC_EVENTO,SIT_EVENTO,TIP_EVENTO,VAL_EVENTO,SEQ_TRANCHE_CONTRATO)";
    StringBuilder values = new StringBuilder();
    values
        .append("TO_DATE('" + this.dataPlanilha + "','dd/mm/yyyy')" + ",");
    values
        .append("TO_DATE('" + this.dataPlanilha + "','dd/mm/yyyy')" + ",");
    values.append("'',");
    values.append("'" + this.situacaoEvento.toUpperCase().trim() + "',");
    values.append("'" + TipoEventoEnum.DIFERENCA + "',");
    values.append(""+this.valorMoedaOriginal+",");
    values.append(seqTranche);    
    insert = INSERT_INTO + OWNER + "." + tabela + campos + STR_VALUES + values
        + CLOSING;
    return insert;
  }

}
