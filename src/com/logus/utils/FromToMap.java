package com.logus.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.logus.domain.Contract;

public class FromToMap {
	
	private static InheritableThreadLocal<Map<String, String>> depara;
	private static InheritableThreadLocal<Map<String, Contract>> deparaDividas;
	static {
		FromToMap.depara = new InheritableThreadLocal<Map<String,String>>();
		depara.set(new HashMap<String, String>());
		FromToMap.deparaDividas = new InheritableThreadLocal<Map<String,Contract>>();
		deparaDividas.set(new HashMap<String, Contract>());
	}
	public static Map<String, String> init() {
		depara.get().put("PROMORADIA III", "PRO-MORADIA III");
		depara.get().put("SANEAMENTO PARA TODOS", "SANEAMENTO PARA TODOS I - SÃO PEDRO DO PI");
		depara.get().put("PRO-MORADIA I", "PRO-MORADIA I");
		depara.get().put("PROMORADIA II", "PRO-MORADIA II");
		depara.get().put("SAN. P/TODOS II PARNAIBA", "SANEAMENTO PARA TODOS II - PARNAÍBA");
		depara.get().put("SAN. P/ TODOS II-TERESINA", "SANEAMENTO PARA TODOS II - TERESINA");
		depara.get().put("SANEAMENTO P/TODOS-MARCOL", "SANEAMENTO PARA TODOS I - MARCOLÂNDIA");
		depara.get().put("SANEAMENTO P/TODOS I", "SANEAMENTO PARA TODOS I - PICOS");
		depara.get().put("SANEAMENTO P/TODOS I-S.FC", "SANEAMENTO PARA TODOS I - SÃO FRANCISCO DO PI");
		depara.get().put("SAN. P/TODOS I-S. JOÃO PI", "SANEAMENTO PARA TODOS I - SÃO JOÃO DO PI");
		depara.get().put("SAN. PARA TODOS I - UNIÃO", "SANEAMENTO PARA TODOS I - UNIÃO");
		depara.get().put("SANEAMENTO PARA TODOS III", "SANEAMENTO PARA TODOS III - PARNAÍBA");
		depara.get().put("PROTRANSPORTES - CAIXA", "PROTRANSPORTE");
		depara.get().put("FINAC. INVEST. - FINISA I", "FINANCIAMENTO À INFRAESTRUTURA E SANEAMENTO - FINISA I");
		depara.get().put("FINISA II", "FINANCIAMENTO À INFRAESTRUTURA E SANEAMENTO - FINISA II");
		depara.get().put("PARCELAMENTO FCVS/EMGERPI", "");
		depara.get().put("PRODESENVOLVIMENTO II", "PRODESENVOLVIMENTO II");
		depara.get().put("ADITIVO PRODESENVOLV. II", "PRODESENVOLVIMENTO II");
		depara.get().put("PRODETUR II", "PRODETUR II");
		depara.get().put("BNDES - PEF II", "PEF II");
		depara.get().put("PROINVEST", "PROINVEST");
		depara.get().put("PARC L 11941-EMGERPI-PREV", "");
		depara.get().put("PARC L 11941 EMGERPI-OUTR", "");
		depara.get().put("PARC. INSS LEI 12810-EXEC", "");
		depara.get().put("PARC INSS LEI12810-LEGISL", "");
		depara.get().put("PARC INSS LEI 12810 MP", "");
		depara.get().put("INSS SIMPL ORD EDUCAÇÃO", "");
		depara.get().put("PARCELAMENTO INSS SESAPI", "");
		depara.get().put("PARC PASEP LEI 12.810/13", "");
		depara.get().put("PARC EMGERPI LEI 12996 PR", "");
		depara.get().put("PARC. EMGERPI LEI12996 TR", "");
		depara.get().put("PARC SIMPL FGTS EMGERPI", "");
		depara.get().put("PARCEL. EMGERPI ITR/PERT", "");
		depara.get().put("PERT ASSEMBLÉIA SENADO", "");
		depara.get().put("PARC. ORD/SIMPLIF. INSS E", "");
		depara.get().put("PARCELAMENTO SIMPLIF INSS", "");
		depara.get().put("BRB RODOVIAS", "BRB - RODOVIAS");
		depara.get().put("BRB RODOVIAS II", "BRB - RODOVIAS II");
		depara.get().put("PROFISCO - BID", "PROFISCO I");
		depara.get().put("PROFISCO II", "PROFISCO II");
		depara.get().put("BIRD - PCPR II-2A.  ETAPA", "PCPR-II - 2a. ETAPA");
		depara.get().put("DPL - BIRD", "PROGRAMA DE DESENVOLVIMENTO SUSTENTÁVEL - DPL I");
		depara.get().put("DPL II - BIRD", "CRESCIMENTO SUSTENTÁVEL E INCLUSIVO - DPL II");
		depara.get().put("SWAPP - BIRD", "PILARES DE CRESCIMENTO E INCLUSÃO SOCIAL  (SWAp)");
		depara.get().put("VIVA O SEMI ARIDO", "PROGR. DE DESENV. SUSTENTÁVEL NO SEMI-ÁRIDO");
		return depara.get();
	}
	
	public static Map<String, Contract> initDividas() {
		try {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			File myFile = new File("INFODIVIDAS/INFO DÍVIDAS.xls");
			FileInputStream fis;
			fis = new FileInputStream(myFile);
			HSSFWorkbook wb = new HSSFWorkbook(fis);
			HSSFSheet sheet = wb.getSheetAt(0);
			int rows = sheet.getPhysicalNumberOfRows();
            for (int r = 8; r < rows; r++) {
				HSSFRow row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }

                System.out.println("\nROW " + row.getRowNum() + " has " + row.getPhysicalNumberOfCells() + " cell(s).");
                HSSFCell cell = row.getCell(0);
                Contract contract = new Contract();
                if(cell.getStringCellValue().trim().equals("TOTAL")) {
                	break;
                }
                String nomeContrato = cell.getStringCellValue().trim();
                contract.setNome(nomeContrato);                    
				System.out.println(contract);
				cell = row.getCell(1);
				String nomeCredor = cell.getStringCellValue().trim();
				System.out.println(nomeCredor);
				contract.setNomeCredor(nomeCredor);
				cell = row.getCell(2);
				String garantia = cell.getStringCellValue().trim();
				System.out.println(garantia);
				contract.setGarantia(garantia);
				cell = row.getCell(3);
				String numeroContrato = cell.getStringCellValue().trim();
				System.out.println(numeroContrato);
				contract.setContrato(numeroContrato);
				cell = row.getCell(4);
				Date assinaturaContrato = cell.getDateCellValue();
				System.out.println(assinaturaContrato);
				contract.setDataAssinatura(sdf.format(assinaturaContrato));
				cell = row.getCell(5);
				Date terminoContrato = cell.getDateCellValue();
				System.out.println(terminoContrato);
				contract.setDataTermino(sdf.format(terminoContrato));
				cell = row.getCell(6);
				String abrMoeda = cell.getStringCellValue().trim();
				System.out.println(abrMoeda);
				contract.setNomeMoeda(abrMoeda);
				cell = row.getCell(7);
				double numericCellValue = cell.getNumericCellValue();
				contract.setValorContrato(String.valueOf(numericCellValue));
				cell = row.getCell(9);
				double prazo = cell.getNumericCellValue();
				contract.setPrazo(String.valueOf((int)(prazo*12)+1));
				cell = row.getCell(10);
				String sistemaAmortizacao = cell.getStringCellValue().trim();
				System.out.println(sistemaAmortizacao);
				contract.setSistema(sistemaAmortizacao);
				cell = row.getCell(12);
				double saldoDevedorAnoPassado = cell.getNumericCellValue();
				System.out.println(saldoDevedorAnoPassado);
				contract.setSaldoDevedorAnoPassado(saldoDevedorAnoPassado);
				cell = row.getCell(14);
				String descricaoJuros = cell.getStringCellValue();
				System.out.println(descricaoJuros);
				contract.setIndexadorJuros(descricaoJuros);
				cell = row.getCell(16);
				LocalDateTime diaEleito = cell.getLocalDateTimeCellValue();
				System.out.println(diaEleito);
				contract.setDiaEleito(diaEleito.toLocalDate());
				contract.setDataAmortizacao(diaEleito.toLocalDate().format(dtf));
				cell = row.getCell(18);
				String descricaoCorrecao = cell.getStringCellValue();
				System.out.println(descricaoCorrecao);
				contract.setIndexadorCorrecaoMonetaria(descricaoCorrecao);
				cell = row.getCell(20);
				double percentualJuros = cell.getNumericCellValue();
				System.out.println(percentualJuros);
				contract.setPercentualJuros(percentualJuros);
				cell = row.getCell(21);
				double percentualTxCredito = cell.getNumericCellValue();
				System.out.println(percentualTxCredito);
				contract.setPercentualTxCredito(percentualTxCredito);
				deparaDividas.get().put(nomeContrato, contract);
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return deparaDividas.get();
	}
}
