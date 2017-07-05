package org.giste.club.web.service;

import org.giste.club.common.dto.UserDto;
import org.giste.club.web.service.exception.DuplicatedUserMailException;
import org.giste.spring.util.config.RestProperties;
import org.giste.spring.util.error.dto.RestErrorDto;
import org.giste.spring.util.service.CrudRestServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserRestServiceImpl extends CrudRestServiceImpl<UserDto> implements UserRestService {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	/**
	 * Constructs a new implementation for {@link UserRestService}.
	 * 
	 * @param restTemplate RestTemplate used for communicate with REST server.
	 * @param restPropertiesImpl RestProperties with the data for accessing the
	 *            REST server.
	 */
	public UserRestServiceImpl(RestTemplate restTemplate, RestProperties restPropertiesImpl) {
		super(restTemplate, restPropertiesImpl);
	}

	@Override
	protected Class<UserDto[]> getArrayType() {
		return UserDto[].class;
	}

	@Override
	protected Class<UserDto> getDtoType() {
		return UserDto.class;
	}

	@Override
	protected String getBasePath() {
		return "/users";
	}

	@Override
	protected void handleHttpStatusConflict(RestErrorDto error) {
		// Throw DuplicatedUserMailException with message from RestErrorDto.
		LOGGER.debug("Throwing DuplicatedUserMailException");
		throw new DuplicatedUserMailException(error.getMessage());
	}

}
