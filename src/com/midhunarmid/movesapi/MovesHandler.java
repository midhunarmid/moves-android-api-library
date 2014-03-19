package com.midhunarmid.movesapi;

import com.midhunarmid.movesapi.util.MovesStatus;

/**
 * This interface will act as a handler who notifies all success and failures
 * @author Midhu
 *
 * @param <T> : A generic class, which probably will be the response type after success
 * @see MovesStatus
 */
public interface MovesHandler<T> {
	
	/**
	 * Implement this method to get success notifications along with the result
	 * @param result : Result of the operation completed with this handler
	 */
	public void onSuccess(T result);
	
	/**
	 * Implement this method to get failure notifications along with the {@link MovesStatus} code and a brief message
	 * @param status : Status code of the failure
	 * @param message : A brief message about the reason behind failure
	 */
	public void onFailure(MovesStatus status, String message);
}
