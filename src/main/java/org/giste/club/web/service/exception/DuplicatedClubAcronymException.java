package org.giste.club.web.service.exception;

/**
 * Exception thrown when a club is tried to be created with an acronym that it's
 * in use by another club.
 * 
 * @author Nacho
 */
public class DuplicatedClubAcronymException extends Exception {

	private static final long serialVersionUID = 2894647064409676921L;
	private static final String message = "The acronym %s is in use by another club.";

	private String duplicatedAcronym;

	/**
	 * Creates the exception with the acronym used.
	 * 
	 * @param acronym Acronym that is in use by another club.
	 */
	public DuplicatedClubAcronymException(String acronym) {
		super(String.format(message, acronym));
		this.duplicatedAcronym = acronym;
	}

	/**
	 * Gets the acronym that is in use by another club.
	 * 
	 * @return The acronym.
	 */
	public String getDuplicatedAcronym() {
		return duplicatedAcronym;
	}

}
