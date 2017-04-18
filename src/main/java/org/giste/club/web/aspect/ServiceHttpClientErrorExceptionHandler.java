package org.giste.club.web.aspect;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.giste.club.web.service.exception.EntityNotFoundException;
import org.giste.spring.util.error.dto.RestErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class with general handlers for HttpClientErrorExceptions for
 * {@org.giste.club.web.service} package.
 * 
 * @author Giste
 */
@Aspect
@Component
public class ServiceHttpClientErrorExceptionHandler {

	private final Logger LOGGER = LoggerFactory.getLogger(ServiceHttpClientErrorExceptionHandler.class);

	/**
	 * Handles HttpClientErrorException exception if HttpStatus is NOT_FOUND. It
	 * reads the message from the {@link RestErrorDto} inside
	 * HttpClientErrorException and throws a new {@link EntityNotFoundException}
	 * with it.
	 * 
	 * @param hcee The HttpClientErrorException to handle.
	 */
	@AfterThrowing(pointcut = "within(org.giste.club.web.service.*)", throwing = "hcee")
	public void handleHttpClientErrorNotFound(HttpClientErrorException hcee) {
		if (hcee.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
			ObjectMapper objectMapper = new ObjectMapper();
			RestErrorDto error;

			try {
				error = objectMapper.readValue(hcee.getResponseBodyAsByteArray(), RestErrorDto.class);
			} catch (Exception e) {
				// No RestErrorDto inside HttpClientErrorException.
				// Throw original exception.
				LOGGER.debug("handleEntityNotFoundException: Throwing HttpClientErrorException");
				throw hcee;
			}

			// Throw EntityNotFoundException with message from RestErrorDto.
			LOGGER.debug("handleEntityNotFoundException: Throwing EntityNotFoundException");
			throw new EntityNotFoundException(error.getMessage());
		}

		// HttpStatus different from NOT_FOUND. Throw original exception.
		LOGGER.debug("handleEntityNotFoundException: Throwing HttpClientErrorException");
		throw hcee;
	}
}
