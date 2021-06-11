package com.logus.model;

public class Amortizacao
  implements Evento {
  private String dataPlanilha;
  private String valorMoedaOriginal;
  private String valorRealizadoReal;
  private String valorRealizadoDolar;
  private String situacaoEvento;

  private String tabela = "DIV_EVENTO_TRANCHE";
  private String campos = "(SEQ_EVENTO_CONTRATO," + "DAT_OCORRENCIA,"
      + "DAT_PREVISAO," + "DSC_EVENTO," + "SIT_EVENTO," + "TIP_EVENTO,"
      + "VAL_EVENTO," + "SEQ_OBRIGACAO," + "SEQ_PENALIDADE,"
      + "SEQ_TRANCHE_CONTRATO)";

  public Amortizacao(String[] array) {
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
  public String getNomeEvento() {
    return "Amortiza��o";
  }

  @Override
  public String getCodigoEvento() {
    return "AMORT";
  }

  public String dbInsert(int seqTranche, int seqObrigacao) {
    String insert = "";
    String ins = "Insert into ";
    String owner = "DIVIDA_PI_2021";
    String tabela = "DIV_EVENTO_TRANCHE";
    String campos = "(DAT_OCORRENCIA,DAT_PREVISAO,DSC_EVENTO,SIT_EVENTO,TIP_EVENTO,VAL_EVENTO,SEQ_OBRIGACAO,SEQ_TRANCHE_CONTRATO)";
    String strValues = " values (";
    StringBuilder values = new StringBuilder();
    values
        .append("TO_DATE('" + this.dataPlanilha + "','dd/mm/yyyy')" + ",");
    values
        .append("TO_DATE('" + this.dataPlanilha + "','dd/mm/yyyy')" + ",");
    values.append("'',");
    values.append("'" + this.situacaoEvento.toUpperCase().trim() + "',");
    values.append("'" + TipoEventoEnum.AMORTIZACAO + "',");
    values.append("'"+this.valorMoedaOriginal+"',");
    values.append(seqObrigacao + ",");
    values.append(seqTranche);
    String closing = ")";
    insert = ins + owner + "." + tabela + campos + strValues + values
        + closing;
    return insert;
  }

}
