package com.logus.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class FileReaderUtil {
	
	private static InheritableThreadLocal<BufferedReader> lineReader = null;
	
	
	public static BufferedReader get() throws FileNotFoundException, UnsupportedEncodingException {
		if(FileReaderUtil.lineReader == null) {
			String csvFilePath = "dvn40700.csv";
			FileInputStream fis = new FileInputStream(csvFilePath);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader reader = new BufferedReader(isr);
			FileReaderUtil.lineReader = new InheritableThreadLocal<BufferedReader>();
			FileReaderUtil.lineReader.set(reader);
		}
		return FileReaderUtil.lineReader.get();
	}
}
