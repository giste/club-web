package org.giste.club.web.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to be thrown when the club with the requested identifier can't be
 * found.
 * 
 * @author Nacho
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ClubNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -1076412495722838545L;
	private static final String message = "The requested club can't be found (id = %s)";

	private final long idNotFound;

	/**
	 * Constructs the exception with the requested club identifier as a
	 * parameter.
	 * 
	 * @param id The requested club identifier.
	 */
	public ClubNotFoundException(long id) {
		super(String.format(message, id));
		this.idNotFound = id;
	}

	/**
	 * Gets the club identifier that originated this exception.
	 * 
	 * @return The identifier of the club not found.
	 */
	public long getIdNotFound() {
		return idNotFound;
	}

}
