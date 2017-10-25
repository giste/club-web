package org.giste.club.web.controller;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import org.giste.club.common.dto.CategoryDto;
import org.giste.club.web.service.CategoryRestService;
import org.giste.spring.util.controller.CrudControllerTest;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@RunWith(SpringRunner.class)
@WebMvcTest(CategoryController.class)
public class CategoryControllerTest extends CrudControllerTest<CategoryDto> {

	@MockBean
	private CategoryRestService categoryRestService;

	@Override
	protected CategoryRestService getMockService() {
		return categoryRestService;
	}

	@Override
	protected String getBasePath() {
		return "/categories";
	}

	@Override
	protected String getBaseView() {
		return "category";
	}

	@Override
	protected Class<CategoryDto> getDtoType() {
		return CategoryDto.class;
	}

	@Override
	protected CategoryDto getNewDto() {
		// TODO Auto-generated method stub
		return new CategoryDto(1L, "Category 1", 15, 16, false);
	}

	@Override
	protected CategoryDto getInvalidDto(CategoryDto dto) {
		dto.setName("");
		dto.setMinAge(-1);
		dto.setMaxAge(101);

		return dto;
	}

	@Override
	protected void checkInvalidProperties(ResultActions result) throws Exception {
		result.andExpect(model().attributeHasFieldErrors("entity", "minAge", "maxAge"));
	}

	@Override
	protected MockHttpServletRequestBuilder addRequestParams(MockHttpServletRequestBuilder request, CategoryDto dto) {
		return request.param("name", dto.getName())
				.param("minAge", String.valueOf(dto.getMinAge()))
				.param("maxAge", String.valueOf(dto.getMaxAge()))
				.param("mixed", String.valueOf(dto.isMixed()));
	}

	@Override
	protected ResultActions checkModelList(ResultActions result, CategoryDto target) throws Exception {
		return super.checkModelList(result, target)
				.andExpect(model().attribute("entityList", hasItem(allOf(
						hasProperty("name", is(target.getName())),
						hasProperty("minAge", is(target.getMinAge())),
						hasProperty("maxAge", is(target.getMaxAge())),
						hasProperty("mixed", is(target.isMixed()))))));
	}

	@Override
	protected ResultActions checkModel(ResultActions result, CategoryDto target) throws Exception {
		return super.checkModel(result, target)
				.andExpect(model().attribute("entity", hasProperty("name", is(target.getName()))))
				.andExpect(model().attribute("entity", hasProperty("minAge", is(target.getMinAge()))))
				.andExpect(model().attribute("entity", hasProperty("maxAge", is(target.getMaxAge()))))
				.andExpect(model().attribute("entity", hasProperty("mixed", is(target.isMixed()))));
	}

	@Override
	protected void checkDto(CategoryDto dto, CategoryDto target, boolean checkId) {
		super.checkDto(dto, target, checkId);
		assertThat(dto.getName(), is(target.getName()));
		assertThat(dto.getMinAge(), is(target.getMinAge()));
		assertThat(dto.getMaxAge(), is(target.getMaxAge()));
		assertThat(dto.isMixed(), is(target.isMixed()));
	}
}
