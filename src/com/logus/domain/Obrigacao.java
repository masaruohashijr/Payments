package com.logus.domain;

public class Obrigacao {
	private Integer id;
	private String nome;
	private String codigo;

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

	public String dbInsert(Tranche tranche) {
		String insert = "";
		String tabela = "DIV_OBRIGACAO";
		String campos = "(cod_obrigacao, dat_inicial, "
				+ "exp_incidencia, exp_quitacao, nm_obrigacao, tip_periodicidade, seq_tranche)";
		StringBuilder values = new StringBuilder();
		values.append("'" + getCodigo() + "',");
		values.append("TO_DATE('" + tranche.getContrato().getDataAssinatura() + "','dd/mm/yyyy')" + ",");
		values.append("'0',");
		values.append("'0',");
		values.append("'" + getNome() + "',");
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

}
