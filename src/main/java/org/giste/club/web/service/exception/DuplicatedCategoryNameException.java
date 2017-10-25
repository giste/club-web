package org.giste.club.web.service.exception;

/**
 * Exception thrown when a category is tried to be created or updated with a
 * name that it's in use by another category.
 * 
 * @author Giste
 */
public class DuplicatedCategoryNameException extends RuntimeException {

	private static final long serialVersionUID = 4604363927475543513L;

	/**
	 * Creates the exception with the message.
	 * 
	 * @param message Message for this exception.
	 */
	public DuplicatedCategoryNameException(String message) {
		super(message);
	}
}
