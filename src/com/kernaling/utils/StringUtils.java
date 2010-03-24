package com.kernaling.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	private static Pattern vaildatePattern = Pattern.compile(
			"http://edu.qq.com", Pattern.DOTALL);
	private static Pattern domainPattern = Pattern.compile("http://(.+?)//*",
			Pattern.DOTALL);

	public static boolean isVaildURL(String url) {
		Matcher matcher = vaildatePattern.matcher(url);
		return matcher.find();
	}
	
	public static String MD5(String strSrc) {

		MessageDigest md = null;
		// 加密后的字符串
		String strDes = null;
		// 要加密的字符串字节型数组
		byte[] bt = strSrc.getBytes();
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(bt);
			// 通过执行诸如填充之类的最终操作完成哈希计算
			strDes = StringUtils.bytes2Hex(md.digest()); // to HexString
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Invalid algorithm.\n" + e.getMessage());
			return null;
		}
		return strDes;
	}

	// 将字节数组转换成16进制的字符串
	private static String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;

		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}

	public static String getDomain(String url) {
		if (url == null || !url.startsWith("http://")) {
			return null;
		}

		Matcher tMatch = domainPattern.matcher(url);
		if (tMatch.find()) {
			String tDomain = tMatch.group(1).trim();
			if (tDomain.equals("")) {
				return null;
			}

			return tDomain;
		}
		return null;
	}

	public static String PatternFind(String pattern, String text) {
		Matcher matcher = Pattern.compile(pattern, Pattern.DOTALL)
				.matcher(text);

		if (matcher.find()) {
			return matcher.group(1).trim();
		}
		return "";
	}

	public static Map<Integer, String> PatternFindMutil(String pattern,
			String text) {

		Matcher tPattern = Pattern.compile(pattern, Pattern.DOTALL).matcher(
				text);

		int groupCount = tPattern.groupCount();

		Map<Integer, String> map = new HashMap<Integer, String>();
		int times = 0;
		while (tPattern.find()) {

			for (int i = 0; i < groupCount; i++) {
				String tValue = tPattern.group(i).trim();
				map.put(times + i, tValue);
			}

			times += groupCount;
		}

		return map;
	}

	public static String ToSBC(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == ' ') {
				c[i] = '\u3000';
			} else if (c[i] < '\177') {
				c[i] = (char) (c[i] + 65248);

			}
		}
		return new String(c);
	}

	public static String ToDBC(String input) {

		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == '\u3000') {
				c[i] = ' ';
			} else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
				c[i] = (char) (c[i] - 65248);

			}
		}
		String returnString = new String(c);

		return returnString;
	}

	/**
	 * 
	 * @param input
	 * @return by kernaling.wong@gmail.com 2010-03-22 格式化内容
	 */
	public static String formate(String input) {
		if (input == null || input.isEmpty()) {
			return "";
		}

		input = ToDBC(input);

		input = input.replaceAll("<.+?>", "").replaceAll("\\s+", " ").replace(
				"&nbsp", "").replace("-+", "-").replace("【", "[").replace("】",
				"]").replace("《", "<").replace("》", ">").replace("“", "\"")
				.replace("”", "\"").replace("？", "?").replace("、", ",")
				.replace("。", ".").replace("，", ",").replace("『", "[").replace(
						"』", "]").replace("（", "(").replace("）", ")").replace(
						"：", ":");

		return input.trim();
	}

	public static void main(String[] args) {
		// String text = "sfswr(2390)00(333)sdfsdfsd";
		// String pattern = "(2390)00(333)";
		// Map<Integer,String> map = StringUtils.PatternFindMutil(pattern,
		// text);
		//		
		// System.out.println(map);

		System.out.println(MD5("6545113231dfsfsfss"));

	}

}
