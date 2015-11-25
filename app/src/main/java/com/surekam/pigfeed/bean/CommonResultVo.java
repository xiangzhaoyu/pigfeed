package com.surekam.pigfeed.bean;

/**
 * 通用处理结果
 */
public class CommonResultVo {
	
	public static final String ERROR_CODE_SUCCESS = "0";
	public static final String ERROR_CODE_FAILED = "1";
	public static final String ERROR_CODE_FAILED_NO_LOGIN = "-1";

	/**
	 * 错误码
	 */
	public String errorCode = "0";
	
	/**
	 * 消息
	 */
	public String msg = "";
	
	/**
	 * 错误消息
	 */
	public String errorMsg = "";
	
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	/**
	 * 获取成功的vo
	 * @param msg
	 * @return
	 */
	public static CommonResultVo buildSuccess(String msg) {
		CommonResultVo vo = new CommonResultVo();
		vo.setErrorCode(ERROR_CODE_SUCCESS);
		vo.setMsg(msg);
		return vo;
	}
	

	/**
	 * 获取失败的vo
	 * @param msg
	 * @return
	 */
	public static CommonResultVo buildError(String errorMsg) {
		CommonResultVo vo = new CommonResultVo();
		vo.setErrorCode(ERROR_CODE_FAILED);
		vo.setErrorMsg(errorMsg);
		return vo;
	}
	
	/**
	 * 获取没有登录vo
	 * @param msg
	 * @return
	 */
	public static CommonResultVo buildNotLogin() {
		CommonResultVo vo = new CommonResultVo();
		vo.setErrorCode(ERROR_CODE_FAILED_NO_LOGIN);
		vo.setErrorMsg("还没有登录哦...");
		return vo;
	}
	
	/**
	 * 获取Json
	 * @param vo
	 * @return
	 */
	public static String getJson(CommonResultVo vo) {
		//return JsonMapper.nonEmptyMapper().toJson(vo);
		return "";
	}
}
