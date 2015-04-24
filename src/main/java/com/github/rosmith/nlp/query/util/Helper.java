package com.github.rosmith.nlp.query.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Helper {

	private static final Logger LOG = LoggerFactory.getLogger(Helper.class);

	public static <T> T convert(Class<T> clzz, Object numString) {
		if (clzz == Integer.class) {
			return clzz.cast(Integer.parseInt(numString.toString()));
		} else if (clzz == Double.class) {
			return clzz.cast(Double.parseDouble(numString.toString()));
		} else if (clzz == Float.class) {
			return clzz.cast(Float.parseFloat(numString.toString()));
		} else if (clzz == Long.class) {
			return clzz.cast(Long.parseLong(numString.toString()));
		} else if (clzz == Boolean.class) {
			return clzz.cast(Boolean.parseBoolean(numString.toString()));
		}
		return clzz.cast(numString);
	}

	public static Class<?> determineType(Object numString) {
		try {
			Integer.parseInt(numString.toString());
			return Integer.class;
		} catch (Exception e) {
		}
		try {
			Double.parseDouble(numString.toString());
			return Double.class;
		} catch (Exception e) {
		}
		try {
			Float.parseFloat(numString.toString());
			return Float.class;
		} catch (Exception e) {
		}
		try {
			Long.parseLong(numString.toString());
			return Long.class;
		} catch (Exception e) {
		}
		if (numString.toString().toLowerCase().matches("(true|false)")) {
			return Boolean.class;
		}
		return String.class;
	}

	public static String content(String file) throws IOException {
		LOG.info(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), "UTF-8"));

		String line = "";
		StringBuffer result = new StringBuffer();
		while ((line = br.readLine()) != null) {
			result.append(line);
			result.append("\n");
		}

		br.close();

		return result.toString();
	}

}
