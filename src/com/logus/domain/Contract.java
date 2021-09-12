package com.logus.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.logus.utils.RepositoryUtil;

public class Contract {

	private Collection<Evento> eventos = new ArrayList<Evento>();
	private Integer id;
	private String contrato;
	private String nome;
	private String valorContrato;
	private String dataAssinatura;
	private String dataTermino;
	private String nomeCredor;
	private String garantia; 
	private String sistema; 
	private String contCred;
	private String situacao;
	private String nomeMoeda;
	private String indexador;
	private String tipoDivida;
	private String examContr;
	private String tipoAmortizacao;
	private String dataAmortizacao;
	private String prazo;
	private String carenciaMeses;
	private String periodicidadeQuitacao;
	private String saldoDevedor;
	private Tranche tranche;
	private Map<String, Moeda> currenciesMap;
	private InstituicaoFinanceira institution;
	private double saldoDevedorAnoPassado;

	public Contract() {
		super();
	}

	public Contract(String[] array) {
		super();
		this.contrato = array[0].trim();
		this.nome = array[1].trim();
		if(this.contrato.equals("10100007")) {
			this.valorContrato = "259200000";
		} else if(this.contrato.equals("10100010")) {
			this.valorContrato = "624639291,6";			
		} else {
			this.valorContrato = array[2].trim();
		}
		this.dataAssinatura = array[3].trim();
		this.nomeCredor = array[4].trim();
		this.contCred = array[5].trim();
		this.situacao = array[6].trim();
		this.nomeMoeda = array[7].trim();
		this.indexador = array[8].trim();
		this.tipoDivida = array[9].trim();
		this.examContr = array[10].trim();
		this.tipoAmortizacao = array[11].trim();
		this.dataAmortizacao = array[12].trim();
		this.prazo = array[13].trim();
		this.carenciaMeses = array[14].trim();
		this.periodicidadeQuitacao = array[15].trim();
		this.saldoDevedor = array[16].trim();
	}

	public String getContrato() {
		return contrato;
	}

	public void setContrato(String contrato) {
		this.contrato = contrato;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getValorContrato() {
		return valorContrato;
	}

	public void setValorContrato(String valorContrato) {
		this.valorContrato = valorContrato;
	}

	public String getDataAssinatura() {
		return dataAssinatura;
	}

	public void setDataAssinatura(String dataAssinatura) {
		this.dataAssinatura = dataAssinatura;
	}

	public String getNomeCredor() {
		return nomeCredor;
	}

	public void setNomeCredor(String nomeCredor) {
		this.nomeCredor = nomeCredor;
	}

	public String getContCred() {
		return contCred;
	}

	public void setContCred(String contCred) {
		this.contCred = contCred;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public String getNomeMoeda() {
		return nomeMoeda;
	}

	public void setNomeMoeda(String nomeMoeda) {
		this.nomeMoeda = nomeMoeda;
	}

	public String getIndexador() {
		return indexador;
	}

	public void setIndexador(String indexador) {
		this.indexador = indexador;
	}

	public String getTipoDivida() {
		return tipoDivida;
	}

	public void setTipoDivida(String tipoDivida) {
		this.tipoDivida = tipoDivida;
	}

	public String getExamContr() {
		return examContr;
	}

	public void setExamContr(String examContr) {
		this.examContr = examContr;
	}

	public String getTipoAmortizacao() {
		return tipoAmortizacao;
	}

	public void setTipoAmortizacao(String tipoAmortizacao) {
		this.tipoAmortizacao = tipoAmortizacao;
	}

	public String getDataAmortizacao() {
		return dataAmortizacao;
	}

	public void setDataAmortizacao(String dataAmortizacao) {
		this.dataAmortizacao = dataAmortizacao;
	}

	public String getPrazo() {
		return prazo;
	}

	public void setPrazo(String prazo) {
		this.prazo = prazo;
	}

	public String getCarenciaMeses() {
		return carenciaMeses;
	}

	public void setCarenciaMeses(String carenciaMeses) {
		this.carenciaMeses = carenciaMeses;
	}

	public String getPeriodicidadeQuitacao() {
		return periodicidadeQuitacao;
	}

	public void setPeriodicidadeQuitacao(String periodicidadeQuitacao) {
		this.periodicidadeQuitacao = periodicidadeQuitacao;
	}

	public String getSaldoDevedor() {
		return saldoDevedor;
	}

	public void setSaldoDevedor(String saldoDevedor) {
		this.saldoDevedor = saldoDevedor;
	}

	@Override
	public String toString() {
		return this.nome;
	}

	public void add(Evento evento) {
		eventos.add(evento);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Tranche getTranche() {
		return tranche;
	}

	public void setTranche(Tranche tranche) {
		this.tranche = tranche;
	}

	public String dbInsert(Moeda currency, Credor creditor, Finalidade finality) {
		String insert = "";
		String tabela = "DIV_CONTRATO";
		String campos = "(DAT_INICIAL,NOM_CONTRATO,VAL_CONTRATO,SEQ_FINALIDADE_OPERACAO,SEQ_MOEDA_CONTRATADA,SEQ_CREDOR)";
		StringBuilder values = new StringBuilder();
		values.append("TO_DATE('" + this.dataAssinatura + "','dd/mm/yyyy')" + ",");
		values.append("'" + this.nome + "',");
		values.append("'" + this.valorContrato + "',");
		values.append(finality.getId() + ",");
		values.append(currency.getId() + ",");
		values.append(creditor.getId());
		insert = DBInserter.INSERT_INTO + DBInserter.OWNER + "." + tabela + campos + DBInserter.STR_VALUES + values
				+ DBInserter.CLOSING;
		return insert;
	}

	public void setInstituicaoFinanceira(InstituicaoFinanceira institution) {
		this.institution = institution;
	}

	public InstituicaoFinanceira getInstitution() {
		return institution;
	}

	public void setInstitution(InstituicaoFinanceira institution) {
		this.institution = institution;
	}

	public String getGarantia() {
		return garantia;
	}

	public void setGarantia(String garantia) {
		this.garantia = garantia;
	}

	public String getSistema() {
		return sistema;
	}

	public void setSistema(String sistema) {
		this.sistema = sistema;
	}

	public String getDataTermino() {
		return dataTermino;
	}

	public void setDataTermino(String dataTermino) {
		this.dataTermino = dataTermino;
	}

	public void setSaldoDevedorAnoPassado(double saldoDevedorAnoPassado) {
		this.saldoDevedorAnoPassado = saldoDevedorAnoPassado;
	}

	public double getSaldoDevedorAnoPassado() {
		return saldoDevedorAnoPassado;
	}
	
}
