package com.team4.exceptions;

import com.team4.utils.exceptions.T4Code;

public class ErrorCode extends T4Code {
	
	//标识APP中抛出的各种异常编码
	public static final int APP_ERROR_PARAM_INVALID = T4Code.APP_ERROR + 1;
	
	//解析JSON时抛出的各种异常编码
	public static final int PARSE_ERROR_FORMAT_INVALID = T4Code.PARSE_ERROR + 1;
	public static final int PARSE_ERROR_PARSER_INVALID = T4Code.PARSE_ERROR + 2;
	
	//下载异常编码
	public static final int DOWNLOAD_OK = 100000;
	public static final int DOWNLOAD_CANCEL = 100001;
	public static final int DOWNLOAD_FAILED = 100002;
}
