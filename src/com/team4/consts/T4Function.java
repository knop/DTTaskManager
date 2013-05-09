package com.team4.consts;

import java.io.File;

public class T4Function {

	public static String getFileNameFromPath(String path) {
		if (path == null)
			return "";
		int pos = path.lastIndexOf("/");
		String fileName = path.substring(pos + 1);
		return fileName;
	}

	public static String rootFolderPath() {
		String path = T4Const.rootFolderPath;
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}
	
	public static String getMIMEType(File file) {
		String type = "*/*";
		String name = file.getName();
		// 获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = name.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* 获取文件的后缀名 */
		String end = name.substring(dotIndex);
		if (end == "")
			return type;
		// 在MIME和文件类型的匹配表中找到对应的MIME类型。
		for (int i = 0; i < T4Const.MIME_MapTable.length; i++) {
			if (end.equalsIgnoreCase(T4Const.MIME_MapTable[i][0]))
				type = T4Const.MIME_MapTable[i][1];
		}
		return type;
	}
	
	public static boolean checkFileTypeInStringArray(String checkItsEnd,
			String[] fileEndings) {
		for (String aEnd : fileEndings) {
			if (checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}

}
