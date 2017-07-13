package org.giste.club.web.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import org.giste.club.common.dto.Role;
import org.giste.club.common.dto.UserDto;
import org.giste.club.web.service.exception.DuplicatedUserMailException;
import org.giste.spring.util.config.RestProperties;
import org.giste.spring.util.error.dto.FieldErrorDto;
import org.giste.spring.util.error.dto.RestErrorDto;
import org.giste.spring.util.service.CrudRestServiceTest;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

public class UserRestServiceTest extends CrudRestServiceTest<UserDto> {

	@Override
	protected UserDto[] getEmptyDtoArray(int size) {
		return new UserDto[size];
	}

	@Override
	protected UserDto getNewDto() {
		return new UserDto(1L, "user1@mail.com", "user1", "123456", Role.ADMIN);
	}

	@Override
	protected String getServiceBasePath() {
		return "/users";
	}

	@Override
	protected UserRestService getRestService(RestTemplate restTemplate, RestProperties restPropertiesImpl) {
		return new UserRestServiceImpl(restTemplate, restPropertiesImpl);
	}

	@Override
	protected RestErrorDto getErrorForInvalidDto() {
		RestErrorDto error = new RestErrorDto(HttpStatus.BAD_REQUEST, "10001001", "Message", "Developer information");
		error.addFieldError(new FieldErrorDto("mail", "Error in mail"));
		error.addFieldError(new FieldErrorDto("role", "Error in role"));

		return error;
	}

	@Test
	public void testCreateDuplicatedMail() throws Exception {
		final UserDto user = new UserDto(1L, "user1@mail.com", "user1", "123456", Role.ADMIN);
		final RestErrorDto error = new RestErrorDto(HttpStatus.CONFLICT, "1", "Message", "Developer info");

		final UriComponents uri = getUriBuilder().path(getPathBase()).build();

		getMockServer().expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.CONFLICT)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.body(getObjectMapper().writeValueAsBytes(error)));

		try {
			getService().create(user);
			fail("Expected DuplicatedUserMailException");
		} catch (DuplicatedUserMailException e) {
			assertThat(e.getMessage(), is(error.getMessage()));
		}

		getMockServer().verify();
	}

	@Test
	public void testUpdateDuplicatedMail() throws Exception {
		final UserDto user = new UserDto(1L, "user1@mail.com", "user1", "123456", Role.ADMIN);
		final RestErrorDto error = new RestErrorDto(HttpStatus.CONFLICT, "1", "Message", "Developer info");

		final UriComponents uri = getUriBuilder().path(getPathId()).buildAndExpand(user.getId());

		getMockServer().expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.PUT))
				.andRespond(withStatus(HttpStatus.CONFLICT)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.body(getObjectMapper().writeValueAsBytes(error)));

		try {
			getService().update(user);
			fail("Expected DuplicatedUserMailException");
		} catch (DuplicatedUserMailException e) {
			assertThat(e.getMessage(), is(error.getMessage()));
		}

		getMockServer().verify();
	}
}
