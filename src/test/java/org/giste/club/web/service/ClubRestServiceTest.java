package org.giste.club.web.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import org.giste.club.common.dto.ClubDto;
import org.giste.club.web.config.RestProperties;
import org.giste.spring.util.error.dto.FieldErrorDto;
import org.giste.spring.util.error.dto.RestErrorDto;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

/**
 * Tests for {@link ClubRestService}
 * 
 * @author Giste
 */
public class ClubRestServiceTest extends CrudeRestServiceTest<ClubDto> {

	@Test
	public void testCreateDuplicatedAcronym() throws Exception {
		final ClubDto club1 = new ClubDto(1L, "Club 1", "CLUB1", false);
		final RestErrorDto error = new RestErrorDto(HttpStatus.CONFLICT, "1", "Message", "Developer info");

		final UriComponents uri = getUriBuilder().path(getPathBase()).build();

		getMockServer().expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.CONFLICT)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.body(getObjectMapper().writeValueAsBytes(error)));

		try {
			getService().create(club1);
			fail("Expected HttpClientErrorException");
		} catch (HttpClientErrorException e) {
			assertThat(e.getStatusCode(), is(HttpStatus.CONFLICT));
			RestErrorDto readError = getObjectMapper().readValue(e.getResponseBodyAsByteArray(), RestErrorDto.class);
			assertThat(readError.getStatus(), is(error.getStatus()));
			assertThat(readError.getCode(), is(error.getCode()));
			assertThat(readError.getMessage(), is(error.getMessage()));
			assertThat(readError.getDeveloperInfo(), is(error.getDeveloperInfo()));
		}

		getMockServer().verify();
	}

	@Test
	public void testUpdateDuplicatedAcronym() throws Exception {
		final ClubDto club1 = new ClubDto(1L, "Club 1", "CLUB1", false);
		final RestErrorDto error = new RestErrorDto(HttpStatus.CONFLICT, "1", "Message", "Developer info");

		final UriComponents uri = getUriBuilder().path(getPathId()).buildAndExpand(club1.getId());

		getMockServer().expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.PUT))
				.andRespond(withStatus(HttpStatus.CONFLICT)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.body(getObjectMapper().writeValueAsBytes(error)));

		try {
			getService().update(club1);
			fail("Expected HttpClientErrorException");
		} catch (HttpClientErrorException e) {
			assertThat(e.getStatusCode(), is(HttpStatus.CONFLICT));
			RestErrorDto readError = getObjectMapper().readValue(e.getResponseBodyAsByteArray(), RestErrorDto.class);
			assertThat(readError.getStatus(), is(error.getStatus()));
			assertThat(readError.getCode(), is(error.getCode()));
			assertThat(readError.getMessage(), is(error.getMessage()));
			assertThat(readError.getDeveloperInfo(), is(error.getDeveloperInfo()));
		}

		getMockServer().verify();
	}

	@Override
	protected CrudeRestService<ClubDto> getRestService(RestTemplate restTemplate, RestProperties restProperties) {
		return new ClubRestServiceImpl(restTemplate, restProperties);
	}

	@Override
	protected String getServiceBasePath() {
		return "/clubs";
	}

	@Override
	protected ClubDto getNewDto() {
		return new ClubDto(1L, "Club 1", "CLB1", true);
	}

	@Override
	protected ClubDto[] getEmptyDtoArray(int size) {
		return new ClubDto[size];
	}

	@Override
	protected void checkProperties(ClubDto dto, ClubDto targetDto) {
		super.checkProperties(dto, targetDto);
		assertThat(dto.getName(), is(targetDto.getName()));
		assertThat(dto.getAcronym(), is(targetDto.getAcronym()));
	}

	@Override
	protected RestErrorDto getErrorForInvalidDto() {
		RestErrorDto error = new RestErrorDto(HttpStatus.BAD_REQUEST, "10001001", "Message", "Developer information");
		error.addFieldError(new FieldErrorDto("name", "Error in name"));
		error.addFieldError(new FieldErrorDto("acronym", "Error in acronym"));

		return error;
	}

}
