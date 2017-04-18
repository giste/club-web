package org.giste.club.web.service;

import java.util.List;

import org.giste.club.common.dto.ClubDto;
import org.giste.club.web.service.exception.DuplicatedClubAcronymException;

/**
 * Interface for Club actions.
 * 
 * @author Giste
 */
public interface ClubService {
	/**
	 * Retrieves all clubs.
	 * 
	 * @return List populated with existing {@link ClubDto} in the application.
	 */
	List<ClubDto> findAll();
	
	/**
	 * Retrieves one club by its identifier.
	 * 
	 * @param id Identifier for the looked up club.
	 * @return {@link ClubDto} with the retrieved club.
	 */
	ClubDto findById(long id);
	
	/**
	 * Creates a new club in the application.
	 * 
	 * @param club {@link ClubDto} with the data for the new club.
	 * @return {@link ClubDto} of the created club.
	 * @throws DuplicatedClubAcronymException If the acronym of the new club is in use by another club.
	 */
	ClubDto create(ClubDto club) throws DuplicatedClubAcronymException;
	
	/**
	 * Updates the data of an existing club.
	 * 
	 * @param club {@link ClubDto} with the data of the club to update.
	 * @return {@link ClubDto} of the updated club.
	 * @throws DuplicatedClubAcronymException If the acronym of the updated club is in use by another club.
	 */
	ClubDto update(ClubDto club) throws DuplicatedClubAcronymException;
	
	/**
	 * Disables a club in the application. It's data won't be accessible.
	 * 
	 * @param id Identifier of the club to disable.
	 * @return {@link ClubDto} of the disabled club.
	 */
	ClubDto disable(long id);
	
	/**
	 * Enables a club in the application. It's data will be accessible.
	 * 
	 * @param id Identifier of the club to enable.
	 * @return {@link ClubDto} of the enabled club.
	 */
	ClubDto enable(long id);
}
