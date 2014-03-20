package com.midhunarmid.movesapi.util;

import com.midhunarmid.movesapi.MovesHandler;

/**
 * This enum contains some status codes, which possibly sent with the failure notification of {@link MovesHandler}
 * @author Midhu
 * @see MovesHandler
 */
public enum MovesStatus {
	SUCCESS,
	NOT_GRANTED,
	EXPIRED,
	AUTH_FAILED,
	UNEXPECTED_ERROR,
	SERVER_FAILURE,
	INVALID_RESPONSE
}
