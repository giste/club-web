package org.giste.club.web.service.exception;

/**
 * Exception thrown when a club is tried to be created or updated with an
 * acronym that it's in use by another club.
 * 
 * @author Giste
 */
public class DuplicatedUserMailException extends RuntimeException {

	private static final long serialVersionUID = -5548471830896670516L;

	/**
	 * Creates the exception with the message.
	 * 
	 * @param message Message for this exception.
	 */
	public DuplicatedUserMailException(String message) {
		super(message);
	}
}
