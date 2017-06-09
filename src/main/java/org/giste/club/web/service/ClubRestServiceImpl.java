package org.giste.club.web.service;

import org.giste.club.common.dto.ClubDto;
import org.giste.club.web.service.exception.DuplicatedClubAcronymException;
import org.giste.spring.util.config.RestProperties;
import org.giste.spring.util.error.dto.RestErrorDto;
import org.giste.spring.util.service.CrudeRestServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation for {@link ClubRestService} interface.
 * 
 * @author Giste
 */
@Service
public class ClubRestServiceImpl extends CrudeRestServiceImpl<ClubDto> implements ClubRestService {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	/**
	 * Construct a ClubRestService with a RestTemplate and a RestProperties
	 * beans.
	 * 
	 * @param restTemplate RestTemplate used to communicate with REST server.
	 * @param restProperties RestProperties with data about REST server (scheme,
	 *            host, port, path).
	 */
	public ClubRestServiceImpl(RestTemplate restTemplate, RestProperties restProperties) {
		super(restTemplate, restProperties);
	}

	@Override
	protected Class<ClubDto> getDtoType() {
		return ClubDto.class;
	}

	@Override
	protected String getBasePath() {
		return "/clubs";
	}

	@Override
	protected Class<ClubDto[]> getArrayType() {
		return ClubDto[].class;
	}

	@Override
	protected void handleHttpStatusConflict(RestErrorDto error) {
		// Throw DuplicatedClubAcronymException with message from RestErrorDto.
		LOGGER.debug("Throwing DuplicatedClubAcronymException");
		throw new DuplicatedClubAcronymException(error.getMessage());
	}

}
