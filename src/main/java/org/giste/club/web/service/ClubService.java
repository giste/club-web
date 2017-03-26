package org.giste.club.web.service;

import java.util.List;

import org.giste.club.common.dto.ClubDto;
import org.giste.club.web.service.exception.ClubNotFoundException;
import org.giste.club.web.service.exception.DuplicatedClubAcronymException;

public interface ClubService {
	List<ClubDto> findAll();
	
	ClubDto findById(long id) throws ClubNotFoundException;
	
	ClubDto create(ClubDto club) throws DuplicatedClubAcronymException;
	
	ClubDto update(ClubDto club) throws DuplicatedClubAcronymException;
	
	ClubDto disable(long id);
	
	ClubDto enable(long id);
}
