package com.logus.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class FileReaderUtil {
	
	private static InheritableThreadLocal<BufferedReader> lineReader = null;
	
	
	public static BufferedReader get(String csvFilePath) throws FileNotFoundException, UnsupportedEncodingException {
		FileInputStream fis = new FileInputStream(csvFilePath);
		InputStreamReader isr = new InputStreamReader(fis, "ISO-8859-1");
		BufferedReader reader = new BufferedReader(isr);
		FileReaderUtil.lineReader = new InheritableThreadLocal<BufferedReader>();
		FileReaderUtil.lineReader.set(reader);
		return FileReaderUtil.lineReader.get();
	}
}
