package com.logus.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.logus.utils.TextUtils;

public class Obrigacao {
	private Integer id;
	private String nome;
	private String codigo;
	private String expIncidencia = "'0'";
	private String expQuitacao = "'0'";
	private String dtInicioPagamento;
	private String numeroParcelas = "";

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

	public String dbInsert(Tranche tranche, Contract contractInfo) {
		String insert = "";
		String tabela = "DIV_OBRIGACAO";
		String campos = "(cod_obrigacao, dat_inicial, "
				+ "exp_incidencia, exp_quitacao, nom_obrigacao, num_parcelas, tip_periodicidade, seq_tranche)";
		StringBuilder values = new StringBuilder();
		values.append("'" + getCodigo() + "',");
		if(this.nome.equalsIgnoreCase("Amortização")) {
			values.append("TO_DATE('" + this.dtInicioPagamento+"','dd/mm/yyyy')" + ",");
			if (null!=contractInfo && "SAC".equalsIgnoreCase(contractInfo.getSistema())) {
				values.append("'VALOR_LIBERACAO',"); // exp incidencia
				values.append("'TRUNCATE ( SALDO/PARCELAS_RESTANTES )',"); // exp quitacao
			} else if (null!=contractInfo && "PRICE".equalsIgnoreCase(contractInfo.getSistema())) {
				values.append("'VALOR_LIBERACAO',"); // exp incidencia
				double i = tranche.getContrato().getPercentualJuros()/100;
				values.append("'SALDO * POT(1 + ("+i+"/12),PARCELAS_RESTANTES)*("+i+"/12)/(POT(1 + ("+i+"/12),PARCELAS_RESTANTES) - 1) - (SALDO * ("+i+"/12))',"); // exp quitacao
			} else {
				values.append("'0',"); // exp incidencia
				values.append("'0',"); // exp quitacao
			}
		} else if(this.nome.equalsIgnoreCase("Juros")) {
			DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			LocalDate diaEleito = tranche.getContrato().getDiaEleito();
			if (diaEleito == null) {
				diaEleito = LocalDate.parse(tranche.getContrato().getDataAssinatura(), format);
			}
			values.append("TO_DATE('" + TextUtils.padLeftZeros(String.valueOf(diaEleito.getDayOfMonth()), 2) + tranche.getContrato().getDataAssinatura().substring(2)+"','dd/mm/yyyy')" + ",");
			if (null!=contractInfo && "SAC".equalsIgnoreCase(contractInfo.getSistema())) {
				if(contractInfo.getNome().equals("FINANCIAMENTO À INFRAESTRUTURA E SANEAMENTO - FINISA I")) {
					values.append("'SE(E_DIA_UTIL, ((POT (1+INDICE(CDI, VERDADEIRO)/100, 1/252) - 1)+((POT (1+3.5/100, 1/360)-1)))* SALDO_OBRIGACAO(AMORT, FALSO), 0)',"); // exp incidencia
					values.append("'TRUNCATE(SALDO)',"); // exp quitacao
				} else if(contractInfo.getNome().equals("FINANCIAMENTO À INFRAESTRUTURA E SANEAMENTO - FINISA II")) {
					values.append("'SE(E_DIA_UTIL, ((POT (1+INDICE(CDI, VERDADEIRO)/100, 1/252) - 1)*118/100)* SALDO_OBRIGACAO(AMORT,FALSO), 0)',"); // exp incidencia
					values.append("'TRUNCATE(SALDO)',"); // exp quitacao
				} else if(contractInfo.getNome().equals("BRB - RODOVIAS")) {
					values.append("'SE(E_DIA_UTIL, ((POT (1+INDICE(CDI, VERDADEIRO)/100, 1/252) - 1)*180/100)* SALDO_OBRIGACAO(AMORT,FALSO), 0)',"); // exp incidencia
					values.append("'TRUNCATE(SALDO)',"); // exp quitacao
				} else if(contractInfo.getNome().equals("BRB - RODOVIAS II")) {
					values.append("'SE(E_DIA_UTIL, ((POT (1+INDICE(CDI, VERDADEIRO)/100, 1/252) - 1)*180/100)* SALDO_OBRIGACAO(AMORT,FALSO), 0)',"); // exp incidencia
					values.append("'TRUNCATE(SALDO)',"); // exp quitacao
				} else if(contractInfo.getNome().equals("ITAU CAPEX")) {
					values.append("'SE(E_DIA_UTIL, ((POT (1+INDICE(CDI, VERDADEIRO)/100, 1/252) - 1)*120/100)* SALDO_OBRIGACAO(AMORT,FALSO), 0)',"); // exp incidencia
					values.append("'TRUNCATE(SALDO)',"); // exp quitacao
				} else {
					values.append("'SE(E_DIA_UTIL, ((POT (1+INDICE(CDI, VERDADEIRO)/100, 1/252) - 1)*118/100)* SALDO_OBRIGACAO(AMORT,FALSO), 0)',"); // exp incidencia
					values.append("'SALDO-(((POT (1+INDICE(CDI, VERDADEIRO)/100, 1/252) - 1)*118/100)* SALDO_OBRIGACAO(AMORT,FALSO))',"); // exp quitacao
				}
			} else if (null!=contractInfo && "PRICE".equalsIgnoreCase(contractInfo.getSistema())) {
				values.append("TO_DATE('" + this.dtInicioPagamento+"','dd/mm/yyyy')" + ",");
				values.append("'0',"); // exp incidencia
				double i = tranche.getContrato().getPercentualJuros()/100;
				values.append("'SALDO_OBRIGACAO(AMORT, VERDADEIRO)*("+i+"/12)',"); // exp quitacao
			} else {
				values.append("TO_DATE('" + this.dtInicioPagamento+"','dd/mm/yyyy')" + ",");
				values.append("'0',"); // exp incidencia
				values.append("'0',"); // exp quitacao
			}
		} else {
			values.append(this.expIncidencia + ","); // exp incidencia
			values.append(this.expQuitacao + ","); // exp incidencia
		}
		values.append("'" + getNome() + "',");
		if(this.numeroParcelas.isEmpty()) {
			this.numeroParcelas = tranche.getContrato().getPrazo();
		}
		values.append(this.numeroParcelas+",");
		String periodicidadeQuitacao = tranche.getContrato().getPeriodicidadeQuitacao();
		values.append("'"+periodicidadeQuitacao.toUpperCase()+"',");
		values.append(tranche.getId());
		insert = DBInserter.INSERT_INTO + DBInserter.OWNER + "." + tabela + campos + DBInserter.STR_VALUES + values
				+ DBInserter.CLOSING;
		return insert;
	}

	/**
	 * @return {@link #codigo}
	 */
	public String getCodigo() {
		return codigo;
	}

	/**
	 * @param codigo atualiza {@link #codigo}.
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getExpIncidencia() {
		return expIncidencia;
	}

	public void setExpIncidencia(String expIncidencia) {
		this.expIncidencia = expIncidencia;
	}

	public String getExpQuitacao() {
		return expQuitacao;
	}

	public void setExpQuitacao(String expQuitacao) {
		this.expQuitacao = expQuitacao;
	}

	public String getDtInicioPagamento() {
		return dtInicioPagamento;
	}

	public void setDtInicioPagamento(String dtInicioPagamento) {
		this.dtInicioPagamento = dtInicioPagamento;
	}

	public String getNumeroParcelas() {
		return numeroParcelas;
	}

	public void setNumeroParcelas(String numeroParcelas) {
		this.numeroParcelas = numeroParcelas;
	}

}
