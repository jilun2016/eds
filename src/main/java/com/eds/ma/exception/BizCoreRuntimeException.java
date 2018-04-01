package com.eds.ma.exception;

/**
 * 核心业务错误，模块业务异常基类
 * @author gaoyan
 * @Date: 2017/12/24
 */
public class BizCoreRuntimeException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private Object extraInformation;
	
	private Object[] errorContents;
	
	private boolean isNeedLog;
	
	private String errorCode;

	public BizCoreRuntimeException() {

	}

	public BizCoreRuntimeException(String errorCode) {
		super("errorCode=" +errorCode);
		this.errorCode = errorCode;
	}
	
	
	public BizCoreRuntimeException(String errorCode, Object...errorContents) {
		super("errorCode=" +errorCode);
		this.errorCode = errorCode;
		this.errorContents = errorContents;
	}
	
	public BizCoreRuntimeException(String errorCode, String msg) {
		super(msg);
		this.errorCode = errorCode;
		Object[] o = new Object[1];
		o[0] = msg;
		this.errorContents = o;
	}
	
	public BizCoreRuntimeException(String errorCode, String msg, Object extraInformation) {
		super(msg);
		this.errorCode = errorCode;
		this.extraInformation = extraInformation;
	}

	/**
	 * Any additional information about the exception. Generally a
	 * <code>UserDetails</code> object.
	 * 
	 * @return extra information or <code>null</code>
	 */
	public Object getExtraInformation() {
		return extraInformation;
	}

	public void clearExtraInformation() {
		this.extraInformation = null;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public Object[] getErrorContents() {
		return errorContents;
	}

	public boolean isNeedLog() {
		return isNeedLog;
	}
	
}
