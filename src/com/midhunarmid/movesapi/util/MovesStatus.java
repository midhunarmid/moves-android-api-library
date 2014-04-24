package com.midhunarmid.movesapi.util;

import com.midhunarmid.movesapi.MovesHandler;

/**
 * This enum contains some status codes, which possibly sent with the failure notification of {@link MovesHandler}
 * @author Midhu
 * @see MovesHandler
 */
public enum MovesStatus {
	SUCCESS(""),
	NOT_GRANTED(""),
	EXPIRED(""),
	AUTH_FAILED(""),
	UNEXPECTED_ERROR(""),
	BAD_RESPONSE(""),
	INVALID_RESPONSE(""),
	NOT_AUTHENTICATED("");
	
	String statusMessage;
	
	private MovesStatus(String message) {
		statusMessage = "";
	}

	/** Retrieve the status message, blank if no specific message is available **/
	public String getStatusMessage() {
		return statusMessage;
	}

	/** Sets a more specific message for this status **/
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	
	
}
