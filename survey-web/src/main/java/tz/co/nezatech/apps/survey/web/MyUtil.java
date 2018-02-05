package tz.co.nezatech.apps.survey.web;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Random;

public class MyUtil {
	public static String alphaNumericRandom(int len) {
		String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();

		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		}
		return sb.toString();
	}

	public static String encode(String input) {
		try {
			return Base64.getEncoder().encodeToString(input.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String decode(String input) {
		try {
			return new String(Base64.getDecoder().decode(input), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}
}
