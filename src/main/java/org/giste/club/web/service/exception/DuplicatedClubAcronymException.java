package org.giste.club.web.service.exception;

/**
 * Exception thrown when a club is tried to be created with an acronym that it's
 * in use by another club.
 * 
 * @author Giste
 */
public class DuplicatedClubAcronymException extends Exception {

	private static final long serialVersionUID = 2894647064409676921L;

	/**
	 * Creates the exception with the message.
	 * 
	 * @param message Message for this exception.
	 */
	public DuplicatedClubAcronymException(String message) {
		super(message);
	}
}
