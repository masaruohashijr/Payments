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

  String getNomeEvento();

  String getCodigoEvento();

  String dbInsert(int seqTranche, int seqObrigacao);

  String ins = "Insert into ";
  String owner = "DIVIDA_PI_2021";
  String strValues = " values (";
  String QUITACAO_OBRIGACAO = "QUITACAO_OBRIGACAO";
  String LIBERACAO = "LIBERACAO";

  public enum TipoEventoEnum {

    /**
     * Libera��o do recurso contratado.
     */
    LIBERACAO,
    /**
     * Incid�ncia di�ria de juros.
     */
    INCIDENCIA_JUROS,
    /**
     * Pagamento do principal da d�vida.
     */
    AMORTIZACAO,
    /**
     * Pagamento do juros da d�vida.
     */
    JUROS,
    /**
     * Pagamento de encargos da d�vida.
     */
    ENCARGOS;

    @Override
    public String toString() {
      return name();
    }

  }
}
