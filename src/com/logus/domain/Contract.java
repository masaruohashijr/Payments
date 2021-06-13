package com.logus.domain;

import java.util.ArrayList;
import java.util.Collection;

public class Contract {

	private Collection<Evento> eventos = new ArrayList<Evento>();
	private Integer id;
	private String contrato;
	private String nome;
	private String valorContrato;
	private String dataAssinatura;
	private String credor;
	private String contCred;
	private String situacao;
	private String moeda;
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

	public Contract() {
		super();
	}

	public Contract(String[] array) {
		super();
		this.contrato = array[0];
		this.nome = array[1].trim();
		this.valorContrato = array[2];
		this.dataAssinatura = array[3];
		this.credor = array[4];
		this.contCred = array[5];
		this.situacao = array[6];
		this.moeda = array[7];
		this.indexador = array[8];
		this.tipoDivida = array[9];
		this.examContr = array[10];
		this.tipoAmortizacao = array[11];
		this.dataAmortizacao = array[12];
		this.prazo = array[13];
		this.carenciaMeses = array[14];
		this.periodicidadeQuitacao = array[15];
		this.saldoDevedor = array[16];
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

	public String getCredor() {
		return credor;
	}

	public void setCredor(String credor) {
		this.credor = credor;
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

	public String getMoeda() {
		return moeda;
	}

	public void setMoeda(String moeda) {
		this.moeda = moeda;
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

	public String dbInsert() {
		String insert = "";
		String tabela = "DIV_CONTRATO";
		String campos = "(DAT_INICIAL,NM_CONTRATO,VAL_CONTRATO,SEQ_FINALIDADE_OPERACAO,SEQ_MOEDA_CONTRATADA)";
		StringBuilder values = new StringBuilder();
		values.append("TO_DATE('" + this.dataAssinatura + "','dd/mm/yyyy')" + ",");
		values.append("'" + this.nome + "',");
		values.append("'" + this.valorContrato + "',");
		values.append("'" + "3" + "',");
		values.append("'1'");
		insert = DBInserter.INSERT_INTO + DBInserter.OWNER + "." + tabela + campos + DBInserter.STR_VALUES + values
				+ DBInserter.CLOSING;
		return insert;
	}
}
