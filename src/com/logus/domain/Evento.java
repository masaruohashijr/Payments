package com.logus.domain;

public interface Evento extends DBInserter {
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

	String getNome();

	String getCodigo();

	String dbInsert(int seqTranche, int seqObrigacao);

	public enum TipoEventoEnum {

		/**
		 * Quitação de uma obrigação.
		 */
		QUITACAO_OBRIGACAO,
		/**
		 * Liberação do recurso contratado.
		 */
		LIBERACAO,
		/**
		 * Incidência diária de juros.
		 */
		INCIDENCIA_JUROS,
		/**
		 * Pagamento do principal da dívida.
		 */
		AMORTIZACAO,
		/**
		 * Pagamento do juros da dívida.
		 */
		JUROS,
		/**
		 * Pagamento de encargos da dívida.
		 */
		ENCARGOS;

		@Override
		public String toString() {
			return name();
		}

	}
}
