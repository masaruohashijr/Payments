package com.logus.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.logus.domain.Contract;

public class InfoDividas {
	public static void initDeparaDividas() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			File myFile = new File("INFO DÍVIDAS.xls");
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
                
                contract.setNome(cell.getStringCellValue().trim());                    
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
				cell = row.getCell(10);
				String sistemaAmortizacao = cell.getStringCellValue().trim();
				System.out.println(sistemaAmortizacao);
				contract.setSistema(sistemaAmortizacao);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
