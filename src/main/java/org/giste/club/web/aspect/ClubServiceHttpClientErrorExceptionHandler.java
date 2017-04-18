package org.giste.club.web.aspect;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.giste.club.web.service.exception.DuplicatedClubAcronymException;
import org.giste.spring.util.error.dto.RestErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handlers for HttpClientErrorException inside {@link ClubService}
 * implementations.
 * 
 * @author Giste
 */
@Aspect
@Component
public class ClubServiceHttpClientErrorExceptionHandler {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	/**
	 * Handles HttpClientErrorException exception if HttpStatus is CONFLICT and
	 * throws a new {@link DuplicatedClubAcronymException} with the message from
	 * {@link RestErrorDto} inside HttpClientErrorException.
	 * 
	 * @param hcee The HttpClientErrorException to handle.
	 * @throws DuplicatedClubAcronymException The exception to be thrown.
	 */
	@AfterThrowing(pointcut = "within(org.giste.club.web.service.ClubService+)", throwing = "hcee")
	public void handleHttpClientErrorConflict(HttpClientErrorException hcee)
			throws DuplicatedClubAcronymException {

		if (hcee.getStatusCode().equals(HttpStatus.CONFLICT)) {
			ObjectMapper objectMapper = new ObjectMapper();
			RestErrorDto error;

			try {
				error = objectMapper.readValue(hcee.getResponseBodyAsByteArray(),
						RestErrorDto.class);
				LOGGER.debug("handleHttpClientErrorConflict: error={}", error);
			} catch (Exception e) {
				// No RestErrorDto inside HttpClientErrorException.
				// Throw original exception.
				LOGGER.debug("handleHttpClientErrorConflict: Throwing Exception");
				throw hcee;
			}

			// Throw DuplicatedClubAcronymException with message from
			// RestErrorDto.
			LOGGER.debug("handleHttpClientErrorConflict: Throwing DuplicatedClubAcronymException");
			throw new DuplicatedClubAcronymException(error.getMessage());
		}

		// HttpStatus different from CONFLICT. Throw original exception.
		LOGGER.debug("handleHttpClientErrorConflict: Throwing HttpClientErrorException");
		throw hcee;
	}

}
