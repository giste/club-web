package org.giste.club.web.controller;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.giste.club.common.dto.ClubDto;
import org.giste.club.web.service.ClubRestService;
import org.giste.club.web.service.exception.DuplicatedClubAcronymException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@RunWith(SpringRunner.class)
@WebMvcTest(ClubController.class)
public class ClubControllerTest extends CrudeControllerTest<ClubDto> {

	@MockBean
	private ClubRestService clubRestService;

	@Test
	public void createDuplicatedAcronym() throws Exception {
		ClubDto club = new ClubDto(0L, "Club 1", "CLUB1", true);

		when(getMockService().create(any(ClubDto.class)))
				.thenThrow(new DuplicatedClubAcronymException(club.getAcronym()));

		getMockMvc().perform(post(getPathBase())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", String.valueOf(club.getId()))
				.param("name", club.getName())
				.param("acronym", club.getAcronym())
				.param("enabled", String.valueOf((club.isEnabled()))))
				.andExpect(status().isOk())
				.andExpect(view().name(is("club")))
				.andExpect(model().attributeHasFieldErrors("entity", "acronym"));

		verify(getMockService()).create(any(ClubDto.class));
		verifyNoMoreInteractions(getMockService());
	}

	@Test
	public void updateDuplicatedAcronym() throws Exception {
		ClubDto club = new ClubDto(1L, "Club 1", "CLUB1", true);

		when(getMockService().update(any(ClubDto.class)))
				.thenThrow(new DuplicatedClubAcronymException(club.getAcronym()));

		getMockMvc().perform(post(getPathId(), club.getId())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", String.valueOf(club.getId()))
				.param("name", club.getName())
				.param("acronym", club.getAcronym())
				.param("enabled", String.valueOf((club.isEnabled()))))
				.andExpect(status().isOk())
				.andExpect(view().name(is("club")))
				.andExpect(model().attributeHasFieldErrors("entity", "acronym"));

		verify(getMockService()).update(any(ClubDto.class));
		verifyNoMoreInteractions(getMockService());
	}

	@Override
	protected Class<ClubDto> getDtoType() {
		return ClubDto.class;
	}

	@Override
	protected ClubDto getNewDto() {
		return new ClubDto(1L, "Club name", "CLB", false);
	}

	@Override
	protected ClubDto getInvalidDto(ClubDto dto) {
		dto.setName("Cl");
		dto.setAcronym("CBL_1");

		return dto;
	}

	@Override
	protected void checkModelList(ResultActions result, ClubDto target) throws Exception {
		super.checkModelList(result, target);
		result.andExpect(model().attribute("entityList", hasItem(allOf(
				hasProperty("name", is(target.getName())),
				hasProperty("acronym", is(target.getAcronym()))))));
	}

	@Override
	protected void checkModel(ResultActions result, ClubDto target) throws Exception {
		super.checkModel(result, target);
		result.andExpect(model().attribute("entity", hasProperty("name", is(target.getName()))))
				.andExpect(model().attribute("entity", hasProperty("acronym", is(target.getAcronym()))));
	}

	@Override
	protected void checkDto(ClubDto dto, ClubDto target, boolean checkId) {
		super.checkDto(dto, target, checkId);
		assertThat(dto.getName(), is(target.getName()));
		assertThat(dto.getAcronym(), is(target.getAcronym()));
	}

	@Override
	protected void checkInvalidProperties(ResultActions result) throws Exception {
		result.andExpect(model().attributeHasFieldErrors("entity", "name"))
				.andExpect(model().attributeHasFieldErrors("entity", "acronym"));
	}

	@Override
	protected MockHttpServletRequestBuilder addRequestParams(MockHttpServletRequestBuilder request, ClubDto dto) {
		return request.param("name", dto.getName())
				.param("acronym", dto.getAcronym());
	}

	@Override
	protected ClubRestService getMockService() {
		return clubRestService;
	}

	@Override
	protected String getBasePath() {
		return "/clubs";
	}

	@Override
	protected String getBaseView() {
		return "club";
	}
}
