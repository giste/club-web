package org.giste.club.web.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import org.giste.club.common.dto.CategoryDto;
import org.giste.club.web.service.exception.DuplicatedCategoryNameException;
import org.giste.spring.util.config.RestProperties;
import org.giste.spring.util.error.dto.FieldErrorDto;
import org.giste.spring.util.error.dto.RestErrorDto;
import org.giste.spring.util.service.BaseRestService;
import org.giste.spring.util.service.CrudRestServiceTest;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

public class CategoryRestServiceTest extends CrudRestServiceTest<CategoryDto> {

	@Override
	protected CategoryDto[] getEmptyDtoArray(int size) {
		return new CategoryDto[size];
	}

	@Override
	protected CategoryDto getNewDto() {
		return new CategoryDto(1L, "Category 1", 13, 14, true);
	}

	@Override
	protected String getServiceBasePath() {
		return "/categories";
	}

	@Override
	protected BaseRestService<CategoryDto> getRestService(RestTemplate restTemplate,
			RestProperties restPropertiesImpl) {
		return new CategoryRestServiceImpl(restTemplate, restPropertiesImpl);
	}

	@Override
	protected RestErrorDto getErrorForInvalidDto() {
		RestErrorDto error = new RestErrorDto(HttpStatus.BAD_REQUEST, "10001001", "Message", "Developer information");
		error.addFieldError(new FieldErrorDto("minAge", "Error in minimum age"));
		error.addFieldError(new FieldErrorDto("maxAge", "Error in maximum age"));

		return error;
	}

	@Test
	public void testCreateDuplicatedMail() throws Exception {
		final CategoryDto category = new CategoryDto(null, "Category 1", 13, 14, true);
		final RestErrorDto error = new RestErrorDto(HttpStatus.CONFLICT, "1", "Message", "Developer info");

		final UriComponents uri = getUriBuilder().path(getPathBase()).build();

		getMockServer().expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.CONFLICT)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.body(getObjectMapper().writeValueAsBytes(error)));

		try {
			getService().create(category);
			fail("Expected DuplicatedCategoryNameException");
		} catch (DuplicatedCategoryNameException e) {
			assertThat(e.getMessage(), is(error.getMessage()));
		}

		getMockServer().verify();
	}

	@Test
	public void testUpdateDuplicatedMail() throws Exception {
		final CategoryDto category = new CategoryDto(1L, "Category 1", 13, 14, true);
		final RestErrorDto error = new RestErrorDto(HttpStatus.CONFLICT, "1", "Message", "Developer info");

		final UriComponents uri = getUriBuilder().path(getPathId()).buildAndExpand(category.getId());

		getMockServer().expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.PUT))
				.andRespond(withStatus(HttpStatus.CONFLICT)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.body(getObjectMapper().writeValueAsBytes(error)));

		try {
			getService().update(category);
			fail("Expected DuplicatedCategoryNameException");
		} catch (DuplicatedCategoryNameException e) {
			assertThat(e.getMessage(), is(error.getMessage()));
		}

		getMockServer().verify();
	}
}
