package tz.co.nezatech.apps.survey.web.util;

public class FlashData {
	String styleClass, message;
	int resultCode;

	public FlashData(int resultCode, String message) {
		super();
		this.resultCode = resultCode;
		this.message = message;
	}

	public FlashData() {
		super();
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
