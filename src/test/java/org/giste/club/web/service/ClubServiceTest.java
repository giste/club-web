package org.giste.club.web.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;

import org.giste.club.common.dto.ClubDto;
import org.giste.club.web.config.RestProperties;
import org.giste.club.web.service.ClubService;
import org.giste.club.web.service.ClubServiceRest;
import org.giste.spring.util.error.dto.FieldErrorDto;
import org.giste.spring.util.error.dto.RestErrorDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests for {@link ClubService}
 * 
 * @author Giste
 */
public class ClubServiceTest {

	// URIs.
	private static final String SCHEME = "http";
	private static final String HOST = "localhost";
	private static final int PORT = 8080;
	private static final String PATH_REST = "/rest";
	private static final String PATH_CLUBS = "/clubs";
	private static final String PATH_CLUBS_ID = PATH_CLUBS + "/{id}";
	private static final String PATH_CLUBS_ID_DISABLE = PATH_CLUBS_ID + "/disable";
	private static final String PATH_CLUBS_ID_ENABLE = PATH_CLUBS_ID + "/enable";

	private RestTemplate restTemplate;
	private ObjectMapper objectMapper;
	private UriComponentsBuilder uriBuilder;

	private MockRestServiceServer mockServer;
	private RestProperties restProperties = mock(RestProperties.class);

	private ClubService clubService;

	@Before
	public void setUp() {
		restTemplate = new RestTemplate();
		objectMapper = new ObjectMapper();
		when(restProperties.getScheme()).thenReturn(SCHEME);
		when(restProperties.getHost()).thenReturn(HOST);
		when(restProperties.getPort()).thenReturn(PORT);
		when(restProperties.getPath()).thenReturn(PATH_REST);

		clubService = new ClubServiceRest(restTemplate, restProperties);
		mockServer = MockRestServiceServer.bindTo(restTemplate).build();
		uriBuilder = UriComponentsBuilder.newInstance()
				.scheme(restProperties.getScheme())
				.host(restProperties.getHost())
				.port(restProperties.getPort())
				.path(restProperties.getPath());
	}

	@Test
	public void testFindAllIsOk() throws Exception {
		final ClubDto club1 = new ClubDto(1L, "Club 1", "CLUB1", false);
		final ClubDto club2 = new ClubDto(2L, "Club 2", "CLUB2", true);
		final ClubDto[] clubs = { club1, club2 };

		final UriComponents uri = uriBuilder.path(PATH_CLUBS).build();

		mockServer.expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.GET))
				// .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andRespond(withSuccess(objectMapper.writeValueAsBytes(clubs), MediaType.APPLICATION_JSON_UTF8));

		List<ClubDto> clubList = clubService.findAll();

		mockServer.verify();

		assertThat(clubList.size(), is(clubs.length));
		assertThat(clubList.get(0).getId(), is(club1.getId()));
		assertThat(clubList.get(0).getName(), is(club1.getName()));
		assertThat(clubList.get(0).getAcronym(), is(club1.getAcronym()));
		assertThat(clubList.get(0).isEnabled(), is(club1.isEnabled()));
		assertThat(clubList.get(1).getId(), is(club2.getId()));
		assertThat(clubList.get(1).getName(), is(club2.getName()));
		assertThat(clubList.get(1).getAcronym(), is(club2.getAcronym()));
		assertThat(clubList.get(1).isEnabled(), is(club2.isEnabled()));
	}

	@Test
	public void testFindAllIsEmpty() throws Exception {
		final ClubDto[] clubs = {};

		final UriComponents uri = uriBuilder.path(PATH_CLUBS).build();

		mockServer.expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(objectMapper.writeValueAsBytes(clubs), MediaType.APPLICATION_JSON_UTF8));

		List<ClubDto> clubList = clubService.findAll();

		mockServer.verify();

		assertThat(clubList.size(), is(clubs.length));
	}

	@Test
	public void testFindByIdIsOk() throws Exception {
		final ClubDto club1 = new ClubDto(1L, "Club 1", "CLUB1", false);

		final UriComponents uri = uriBuilder.path(PATH_CLUBS_ID).buildAndExpand(club1.getId());

		mockServer.expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(objectMapper.writeValueAsBytes(club1), MediaType.APPLICATION_JSON_UTF8));

		ClubDto readClub = clubService.findById(club1.getId());

		mockServer.verify();

		assertThat(readClub.getId(), is(club1.getId()));
		assertThat(readClub.getName(), is(club1.getName()));
		assertThat(readClub.getAcronym(), is(club1.getAcronym()));
		assertThat(readClub.isEnabled(), is(club1.isEnabled()));
	}

	@Test
	public void testFindByIdClubNotFound() throws Exception {
		RestErrorDto error = new RestErrorDto(HttpStatus.NOT_FOUND, "0", "Message", "Developer info");

		final UriComponents uri = uriBuilder.path(PATH_CLUBS_ID).buildAndExpand(1);

		mockServer.expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.NOT_FOUND)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.body(objectMapper.writeValueAsBytes(error)));

		try {
			clubService.findById(1);
		} catch (HttpClientErrorException e) {
			assertThat(e.getStatusCode(), is(HttpStatus.NOT_FOUND));

			RestErrorDto readError = objectMapper.readValue(e.getResponseBodyAsByteArray(), RestErrorDto.class);
			assertThat(readError.getStatus(), is(error.getStatus()));
			assertThat(readError.getCode(), is(error.getCode()));
			assertThat(readError.getMessage(), is(error.getMessage()));
			assertThat(readError.getDeveloperInfo(), is(error.getDeveloperInfo()));
		}

		mockServer.verify();
	}

	@Test
	public void testCreateIsValid() throws Exception {
		final ClubDto club1 = new ClubDto(1L, "Club 1", "CLUB1", false);

		final UriComponents uri = uriBuilder.path(PATH_CLUBS).build();

		mockServer.expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.POST))
				.andExpect(content().bytes(objectMapper.writeValueAsBytes(club1)))
				.andRespond(withSuccess(objectMapper.writeValueAsBytes(club1), MediaType.APPLICATION_JSON_UTF8));

		ClubDto readClub = clubService.create(club1);

		mockServer.verify();

		assertThat(readClub.getId(), is(club1.getId()));
		assertThat(readClub.getName(), is(club1.getName()));
		assertThat(readClub.getAcronym(), is(club1.getAcronym()));
		assertThat(readClub.isEnabled(), is(club1.isEnabled()));
	}

	@Test
	public void testCreateIsInvalid() throws Exception {
		final ClubDto club1 = new ClubDto(1L, "1", "CLB_1", false);
		final RestErrorDto error = new RestErrorDto(HttpStatus.BAD_REQUEST, "1", "Message", "Developer info");
		error.addFieldError(new FieldErrorDto("Name", "Invalid name"));
		error.addFieldError(new FieldErrorDto("Acronym", "Invalid acronym"));

		List<FieldErrorDto> fieldErrorList = error.getFieldErrorList();

		final UriComponents uri = uriBuilder.path(PATH_CLUBS).build();

		mockServer.expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withBadRequest()
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.body(objectMapper.writeValueAsBytes(error)));

		try {
			clubService.create(club1);
		} catch (HttpClientErrorException e) {
			assertThat(e.getStatusCode(), is(HttpStatus.BAD_REQUEST));
			RestErrorDto readError = objectMapper.readValue(e.getResponseBodyAsByteArray(), RestErrorDto.class);
			assertThat(readError.getStatus(), is(error.getStatus()));
			assertThat(readError.getCode(), is(error.getCode()));
			assertThat(readError.getMessage(), is(error.getMessage()));
			assertThat(readError.getDeveloperInfo(), is(error.getDeveloperInfo()));

			List<FieldErrorDto> readFieldErrorList = readError.getFieldErrorList();
			assertThat(readFieldErrorList.size(), is(fieldErrorList.size()));
			assertThat(readFieldErrorList.get(0).getField(), is(fieldErrorList.get(0).getField()));
			assertThat(readFieldErrorList.get(0).getMessage(), is(fieldErrorList.get(0).getMessage()));
			assertThat(readFieldErrorList.get(1).getField(), is(fieldErrorList.get(1).getField()));
			assertThat(readFieldErrorList.get(1).getMessage(), is(fieldErrorList.get(1).getMessage()));
		}

		mockServer.verify();
	}

	@Test
	public void testCreateDuplicatedAcronym() throws Exception {
		final ClubDto club1 = new ClubDto(1L, "Club 1", "CLUB1", false);
		final RestErrorDto error = new RestErrorDto(HttpStatus.CONFLICT, "1", "Message", "Developer info");

		final UriComponents uri = uriBuilder.path(PATH_CLUBS).build();

		mockServer.expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.CONFLICT)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.body(objectMapper.writeValueAsBytes(error)));

		try {
			clubService.create(club1);
		} catch (HttpClientErrorException e) {
			assertThat(e.getStatusCode(), is(HttpStatus.CONFLICT));
			RestErrorDto readError = objectMapper.readValue(e.getResponseBodyAsByteArray(), RestErrorDto.class);
			assertThat(readError.getStatus(), is(error.getStatus()));
			assertThat(readError.getCode(), is(error.getCode()));
			assertThat(readError.getMessage(), is(error.getMessage()));
			assertThat(readError.getDeveloperInfo(), is(error.getDeveloperInfo()));
		}

		mockServer.verify();
	}

	@Test
	public void testUpdateIsValid() throws Exception {
		final ClubDto club1 = new ClubDto(1L, "Club 1", "CLUB1", false);

		final UriComponents uri = uriBuilder.path(PATH_CLUBS_ID).buildAndExpand(club1.getId());

		mockServer.expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.PUT))
				.andExpect(content().bytes(objectMapper.writeValueAsBytes(club1)))
				.andRespond(withSuccess(objectMapper.writeValueAsBytes(club1), MediaType.APPLICATION_JSON_UTF8));

		ClubDto readClub = clubService.update(club1);

		mockServer.verify();

		assertThat(readClub.getId(), is(club1.getId()));
		assertThat(readClub.getName(), is(club1.getName()));
		assertThat(readClub.getAcronym(), is(club1.getAcronym()));
		assertThat(readClub.isEnabled(), is(club1.isEnabled()));
	}

	@Test
	public void testUpdateClubNotFound() throws Exception {
		final RestErrorDto error = new RestErrorDto(HttpStatus.NOT_FOUND, "0", "Message", "Developer info");
		final ClubDto club1 = new ClubDto(1L, "Club 1", "CLUB1", false);

		final UriComponents uri = uriBuilder.path(PATH_CLUBS_ID).buildAndExpand(club1.getId());

		mockServer.expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.PUT))
				.andRespond(withStatus(HttpStatus.NOT_FOUND)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.body(objectMapper.writeValueAsBytes(error)));

		try {
			clubService.update(club1);
		} catch (HttpClientErrorException e) {
			assertThat(e.getStatusCode(), is(HttpStatus.NOT_FOUND));

			RestErrorDto readError = objectMapper.readValue(e.getResponseBodyAsByteArray(), RestErrorDto.class);
			assertThat(readError.getStatus(), is(error.getStatus()));
			assertThat(readError.getCode(), is(error.getCode()));
			assertThat(readError.getMessage(), is(error.getMessage()));
			assertThat(readError.getDeveloperInfo(), is(error.getDeveloperInfo()));
		}

		mockServer.verify();
	}

	@Test
	public void testUpdateIsInvalid() throws Exception {
		final ClubDto club1 = new ClubDto(1L, "1", "CLB_1", false);
		final RestErrorDto error = new RestErrorDto(HttpStatus.BAD_REQUEST, "1", "Message", "Developer info");
		error.addFieldError(new FieldErrorDto("Name", "Invalid name"));
		error.addFieldError(new FieldErrorDto("Acronym", "Invalid acronym"));

		List<FieldErrorDto> fieldErrorList = error.getFieldErrorList();

		final UriComponents uri = uriBuilder.path(PATH_CLUBS_ID).buildAndExpand(club1.getId());

		mockServer.expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.PUT))
				.andRespond(withBadRequest()
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.body(objectMapper.writeValueAsBytes(error)));

		try {
			clubService.update(club1);
		} catch (HttpClientErrorException e) {
			assertThat(e.getStatusCode(), is(HttpStatus.BAD_REQUEST));
			RestErrorDto readError = objectMapper.readValue(e.getResponseBodyAsByteArray(), RestErrorDto.class);
			assertThat(readError.getStatus(), is(error.getStatus()));
			assertThat(readError.getCode(), is(error.getCode()));
			assertThat(readError.getMessage(), is(error.getMessage()));
			assertThat(readError.getDeveloperInfo(), is(error.getDeveloperInfo()));

			List<FieldErrorDto> readFieldErrorList = readError.getFieldErrorList();
			assertThat(readFieldErrorList.size(), is(fieldErrorList.size()));
			assertThat(readFieldErrorList.get(0).getField(), is(fieldErrorList.get(0).getField()));
			assertThat(readFieldErrorList.get(0).getMessage(), is(fieldErrorList.get(0).getMessage()));
			assertThat(readFieldErrorList.get(1).getField(), is(fieldErrorList.get(1).getField()));
			assertThat(readFieldErrorList.get(1).getMessage(), is(fieldErrorList.get(1).getMessage()));
		}

		mockServer.verify();
	}

	@Test
	public void testUpdateDuplicatedAcronym() throws Exception {
		final ClubDto club1 = new ClubDto(1L, "Club 1", "CLUB1", false);
		final RestErrorDto error = new RestErrorDto(HttpStatus.CONFLICT, "1", "Message", "Developer info");

		final UriComponents uri = uriBuilder.path(PATH_CLUBS_ID).buildAndExpand(club1.getId());

		mockServer.expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.PUT))
				.andRespond(withStatus(HttpStatus.CONFLICT)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.body(objectMapper.writeValueAsBytes(error)));

		try {
			clubService.update(club1);
		} catch (HttpClientErrorException e) {
			assertThat(e.getStatusCode(), is(HttpStatus.CONFLICT));
			RestErrorDto readError = objectMapper.readValue(e.getResponseBodyAsByteArray(), RestErrorDto.class);
			assertThat(readError.getStatus(), is(error.getStatus()));
			assertThat(readError.getCode(), is(error.getCode()));
			assertThat(readError.getMessage(), is(error.getMessage()));
			assertThat(readError.getDeveloperInfo(), is(error.getDeveloperInfo()));
		}

		mockServer.verify();
	}

	@Test
	public void testDisableIsValid() throws Exception {
		final ClubDto club1 = new ClubDto(1L, "Club 1", "CLUB1", false);

		final UriComponents uri = uriBuilder.path(PATH_CLUBS_ID_DISABLE).buildAndExpand(club1.getId());

		mockServer.expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.PUT))
				.andRespond(withSuccess(objectMapper.writeValueAsBytes(club1), MediaType.APPLICATION_JSON_UTF8));

		ClubDto readClub = clubService.disable(club1.getId());

		mockServer.verify();

		assertThat(readClub.getId(), is(club1.getId()));
		assertThat(readClub.getName(), is(club1.getName()));
		assertThat(readClub.getAcronym(), is(club1.getAcronym()));
		assertThat(readClub.isEnabled(), is(club1.isEnabled()));
	}

	@Test
	public void testDisableClubNotFound() throws Exception {
		final RestErrorDto error = new RestErrorDto(HttpStatus.NOT_FOUND, "0", "Message", "Developer info");
		final ClubDto club1 = new ClubDto(1L, "Club 1", "CLUB1", false);

		final UriComponents uri = uriBuilder.path(PATH_CLUBS_ID_DISABLE).buildAndExpand(club1.getId());

		mockServer.expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.PUT))
				.andRespond(withStatus(HttpStatus.NOT_FOUND)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.body(objectMapper.writeValueAsBytes(error)));

		try {
			clubService.disable(club1.getId());
		} catch (HttpClientErrorException e) {
			assertThat(e.getStatusCode(), is(HttpStatus.NOT_FOUND));

			RestErrorDto readError = objectMapper.readValue(e.getResponseBodyAsByteArray(), RestErrorDto.class);
			assertThat(readError.getStatus(), is(error.getStatus()));
			assertThat(readError.getCode(), is(error.getCode()));
			assertThat(readError.getMessage(), is(error.getMessage()));
			assertThat(readError.getDeveloperInfo(), is(error.getDeveloperInfo()));
		}

		mockServer.verify();
	}

	@Test
	public void testEnableIsValid() throws Exception {
		final ClubDto club1 = new ClubDto(1L, "Club 1", "CLUB1", false);

		final UriComponents uri = uriBuilder.path(PATH_CLUBS_ID_ENABLE).buildAndExpand(club1.getId());

		mockServer.expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.PUT))
				.andRespond(withSuccess(objectMapper.writeValueAsBytes(club1), MediaType.APPLICATION_JSON_UTF8));

		ClubDto readClub = clubService.enable(club1.getId());

		mockServer.verify();

		assertThat(readClub.getId(), is(club1.getId()));
		assertThat(readClub.getName(), is(club1.getName()));
		assertThat(readClub.getAcronym(), is(club1.getAcronym()));
		assertThat(readClub.isEnabled(), is(club1.isEnabled()));
	}

	@Test
	public void testEnableClubNotFound() throws Exception {
		final RestErrorDto error = new RestErrorDto(HttpStatus.NOT_FOUND, "0", "Message", "Developer info");
		final ClubDto club1 = new ClubDto(1L, "Club 1", "CLUB1", false);

		final UriComponents uri = uriBuilder.path(PATH_CLUBS_ID_ENABLE).buildAndExpand(club1.getId());

		mockServer.expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.PUT))
				.andRespond(withStatus(HttpStatus.NOT_FOUND)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.body(objectMapper.writeValueAsBytes(error)));

		try {
			clubService.enable(club1.getId());
		} catch (HttpClientErrorException e) {
			assertThat(e.getStatusCode(), is(HttpStatus.NOT_FOUND));

			RestErrorDto readError = objectMapper.readValue(e.getResponseBodyAsByteArray(), RestErrorDto.class);
			assertThat(readError.getStatus(), is(error.getStatus()));
			assertThat(readError.getCode(), is(error.getCode()));
			assertThat(readError.getMessage(), is(error.getMessage()));
			assertThat(readError.getDeveloperInfo(), is(error.getDeveloperInfo()));
		}

		mockServer.verify();
	}

}
