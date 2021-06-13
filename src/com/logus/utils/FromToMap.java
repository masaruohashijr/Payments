package com.logus.utils;

import java.util.Map;

public class FromToMap {
	
	private static InheritableThreadLocal<Map<String, String>> deparaDividas;
	static {
		FromToMap.deparaDividas = new InheritableThreadLocal<Map<String,String>>();
	}
	public static Map<String, String> init() {
		deparaDividas.get().put("PROMORADIA III", "PRO-MORADIA III");
		deparaDividas.get().put("SANEAMENTO PARA TODOS", "SANEAMENTO PARA TODOS I - SÃO PEDRO DO PI");
		deparaDividas.get().put("PRO-MORADIA I", "PRO-MORADIA I");
		deparaDividas.get().put("PROMORADIA II", "PRO-MORADIA II");
		deparaDividas.get().put("SAN. P/TODOS II PARNAIBA", "SANEAMENTO PARA TODOS II - PARNAÍBA");
		deparaDividas.get().put("SAN. P/ TODOS II-TERESINA", "SANEAMENTO PARA TODOS II - TERESINA");
		deparaDividas.get().put("SANEAMENTO P/TODOS-MARCOL", "SANEAMENTO PARA TODOS I - MARCOLÂNDIA");
		deparaDividas.get().put("SANEAMENTO P/TODOS I", "SANEAMENTO PARA TODOS I - PICOS");
		deparaDividas.get().put("SANEAMENTO P/TODOS I-S.FC", "SANEAMENTO PARA TODOS I - SÃO FRANCISCO DO PI");
		deparaDividas.get().put("SAN. P/TODOS I-S. JOÃO P", "SANEAMENTO PARA TODOS I - SÃO JOÃO DO PI");
		deparaDividas.get().put("SAN. PARA TODOS I - UNIÃ", "SANEAMENTO PARA TODOS I - UNIÃO");
		deparaDividas.get().put("SANEAMENTO PARA TODOS III", "SANEAMENTO PARA TODOS III - PARNAÍBA");
		deparaDividas.get().put("PROTRANSPORTES - CAIXA", "PROTRANSPORTE");
		deparaDividas.get().put("FINAC. INVEST. - FINISA I", "FINANCIAMENTO À INFRAESTRUTURA E SANEAMENTO - FINISA I");
		deparaDividas.get().put("FINISA II", "FINANCIAMENTO À INFRAESTRUTURA E SANEAMENTO");
		deparaDividas.get().put("PARCELAMENTO FCVS/EMGERPI", "");
		deparaDividas.get().put("PRODESENVOLVIMENTO II", "PRODESENVOLVIMENTO II");
		deparaDividas.get().put("ADITIVO PRODESENVOLV. II", "PRODESENVOLVIMENTO II");
		deparaDividas.get().put("PRODETUR II", "PRODETUR II");
		deparaDividas.get().put("BNDES - PEF II", "PEF II");
		deparaDividas.get().put("PROINVEST", "PROINVEST");
		deparaDividas.get().put("PARC L 11941-EMGERPI-PREV", "");
		deparaDividas.get().put("PARC L 11941 EMGERPI-OUTR", "");
		deparaDividas.get().put("PARC. INSS LEI 12810-EXEC", "");
		deparaDividas.get().put("PARC INSS LEI12810-LEGISL", "");
		deparaDividas.get().put("PARC INSS LEI 12810 MP", "");
		deparaDividas.get().put("INSS SIMPL ORD EDUCAÇÃO", "");
		deparaDividas.get().put("PARCELAMENTO INSS SESAPI", "");
		deparaDividas.get().put("PARC PASEP LEI 12.810/13", "");
		deparaDividas.get().put("PARC EMGERPI LEI 12996 PR", "");
		deparaDividas.get().put("PARC. EMGERPI LEI12996 TR", "");
		deparaDividas.get().put("PARC SIMPL FGTS EMGERPI", "");
		deparaDividas.get().put("PARCEL. EMGERPI ITR/PERT", "");
		deparaDividas.get().put("PERT ASSEMBLÉIA SENADO", "");
		deparaDividas.get().put("PARC. ORD/SIMPLIF. INSS E", "");
		deparaDividas.get().put("PARCELAMENTO SIMPLIF INSS", "");
		deparaDividas.get().put("BRB RODOVIAS", "BRB - RODOVIAS");
		deparaDividas.get().put("PROFISCO - BID", "PROFISCO I");
		deparaDividas.get().put("PROFISCO II", "PROFISCO II");
		deparaDividas.get().put("BIRD - PCPR II-2A.  ETAPA", "PCPR-II - 2a. ETAPA");
		deparaDividas.get().put("DPL - BIRD", "PROGRAMA DE DESENVOLVIMENTO SUSTENTÁVEL - DPL I");
		deparaDividas.get().put("DPL II - BIRD", "CRESCIMENTO SUSTENTÁVEL E INCLUSIVO - DPL II");
		deparaDividas.get().put("SWAPP - BIRD", "PILARES DE CRESCIMENTO E INCLUSÃO SOCIAL  (SWAp)");
		deparaDividas.get().put("VIVA O SEMI ARIDO", "PROGR. DE DESENV. SUSTENTÁVEL NO SEMI-ÁRIDO");
		return deparaDividas.get();
	}
}
