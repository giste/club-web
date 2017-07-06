package org.giste.club.web.controller;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import org.giste.club.common.dto.Role;
import org.giste.club.common.dto.UserDto;
import org.giste.club.web.service.UserRestService;
import org.giste.spring.util.controller.CrudControllerTest;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest extends CrudControllerTest<UserDto> {

	@MockBean
	private UserRestService userRestService;

	@Override
	protected UserRestService getMockService() {
		return userRestService;
	}

	@Override
	protected String getBasePath() {
		return "/users";
	}

	@Override
	protected String getBaseView() {
		return "user";
	}

	@Override
	protected Class<UserDto> getDtoType() {
		return UserDto.class;
	}

	@Override
	protected UserDto getNewDto() {
		return new UserDto(1L, "user1@email.com", "123456", Role.USER);
	}

	@Override
	protected UserDto getInvalidDto(UserDto dto) {
		dto.setEmail("invalid_mail");

		return dto;
	}

	@Override
	protected void checkInvalidProperties(ResultActions result) throws Exception {
		result.andExpect(model().attributeHasFieldErrors("entity", "email"));
	}

	@Override
	protected MockHttpServletRequestBuilder addRequestParams(MockHttpServletRequestBuilder request, UserDto dto) {
		return request.param("email", dto.getEmail())
				.param("passwordHash", dto.getPasswordHash())
				.param("role", dto.getRole().toString());
	}

	@Override
	protected ResultActions checkModelList(ResultActions result, UserDto target) throws Exception {
		return super.checkModelList(result, target)
				.andExpect(model().attribute("entityList", hasItem(allOf(
						hasProperty("email", is(target.getEmail())),
						hasProperty("passwordHash", is(target.getPasswordHash())),
						hasProperty("role", is(target.getRole()))))));
	}

	@Override
	protected ResultActions checkModel(ResultActions result, UserDto target) throws Exception {
		return super.checkModel(result, target)
				.andExpect(model().attribute("entity", hasProperty("email", is(target.getEmail()))))
				.andExpect(model().attribute("entity", hasProperty("passwordHash", is(target.getPasswordHash()))))
				.andExpect(model().attribute("entity", hasProperty("role", is(target.getRole()))));
	}

	@Override
	protected void checkDto(UserDto dto, UserDto target, boolean checkId) {
		super.checkDto(dto, target, checkId);
		assertThat(dto.getEmail(), is(target.getEmail()));
		assertThat(dto.getPasswordHash(), is(target.getPasswordHash()));
		assertThat(dto.getRole(), is(target.getRole()));
	}

}
