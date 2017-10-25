package org.giste.club.web.service;

import org.giste.club.common.dto.CategoryDto;
import org.giste.club.web.service.exception.DuplicatedCategoryNameException;
import org.giste.spring.util.config.RestProperties;
import org.giste.spring.util.error.dto.RestErrorDto;
import org.giste.spring.util.service.CrudRestServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CategoryRestServiceImpl extends CrudRestServiceImpl<CategoryDto> implements CategoryRestService {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	/**
	 * Constructs a new service for managing categories.
	 * 
	 * @param restTemplate RestTemplate used to communicate with the REST
	 *            server.
	 * @param restPropertiesImpl RestProperties of the REST server.
	 */
	public CategoryRestServiceImpl(RestTemplate restTemplate, RestProperties restPropertiesImpl) {
		super(restTemplate, restPropertiesImpl);
	}

	@Override
	protected Class<CategoryDto[]> getArrayType() {
		return CategoryDto[].class;
	}

	@Override
	protected Class<CategoryDto> getDtoType() {
		return CategoryDto.class;
	}

	@Override
	protected String getBasePath() {
		return "/categories";
	}

	@Override
	protected void handleHttpStatusConflict(RestErrorDto error) {
		// Throw DuplicatedUserMailException with message from RestErrorDto.
		LOGGER.debug("Throwing DuplicatedCategoryNameException");
		throw new DuplicatedCategoryNameException(error.getMessage());
	}

}
