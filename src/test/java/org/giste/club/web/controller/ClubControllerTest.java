package org.giste.club.web.controller;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;

import org.giste.club.common.dto.ClubDto;
import org.giste.club.web.service.ClubRestService;
import org.giste.club.web.service.exception.DuplicatedClubAcronymException;
import org.giste.club.web.service.exception.EntityNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(ClubController.class)
public class ClubControllerTest {
	// URIs.
	private static final String PATH_CLUBS = "/clubs";
	private static final String PATH_CLUBS_ID = PATH_CLUBS + "/{id}";
	private static final String PATH_CLUBS_NEW = "/clubs/new";
	private static final String PATH_CLUBS_ID_DISABLE = PATH_CLUBS_ID + "/disable";
	private static final String PATH_CLUBS_ID_ENABLE = PATH_CLUBS_ID + "/enable";

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ClubRestService clubRestService;

	@Test
	public void findAllIsValid() throws Exception {
		ClubDto club1 = new ClubDto(1L, "Club 1", "CLUB1", false);
		ClubDto club2 = new ClubDto(2L, "Club 2", "CLUB2", false);

		when(clubRestService.findAll()).thenReturn(Arrays.asList(club1, club2));

		mvc.perform(get(PATH_CLUBS))
				.andExpect(status().isOk())
				.andExpect(view().name("clubList"))
				.andExpect(model().attribute("entityList", hasSize(2)))
				.andExpect(model().attribute("entityList", hasItem(
						allOf(
								hasProperty("id", is(club1.getId())),
								hasProperty("name", is(club1.getName())),
								hasProperty("acronym", is(club1.getAcronym())),
								hasProperty("enabled", is(club1.isEnabled()))))))
				.andExpect(model().attribute("entityList", hasItem(
						allOf(
								hasProperty("id", is(club2.getId())),
								hasProperty("name", is(club2.getName())),
								hasProperty("acronym", is(club2.getAcronym())),
								hasProperty("enabled", is(club2.isEnabled()))))));

		verify(clubRestService).findAll();
		verifyNoMoreInteractions(clubRestService);
	}

	@Test
	public void findAllIsEmpty() throws Exception {
		when(clubRestService.findAll()).thenReturn(Arrays.asList());

		mvc.perform(get(PATH_CLUBS))
				.andExpect(status().isOk())
				.andExpect(view().name("clubList"))
				.andExpect(model().attribute("entityList", hasSize(0)));

		verify(clubRestService).findAll();
		verifyNoMoreInteractions(clubRestService);
	}

	@Test
	public void findByIdIsValid() throws Exception {
		ClubDto club1 = new ClubDto(1L, "Club 1", "CLUB1", false);

		when(clubRestService.findById(club1.getId())).thenReturn(club1);

		mvc.perform(get(PATH_CLUBS_ID, 1L))
				.andExpect(status().isOk())
				.andExpect(view().name("club"))
				.andExpect(model().attribute("entity", hasProperty("id", is(club1.getId()))))
				.andExpect(model().attribute("entity", hasProperty("name", is(club1.getName()))))
				.andExpect(model().attribute("entity", hasProperty("acronym", is(club1.getAcronym()))))
				.andExpect(model().attribute("entity", hasProperty("enabled", is(club1.isEnabled()))));

		verify(clubRestService).findById(club1.getId());
		verifyNoMoreInteractions(clubRestService);
	}

	@Test
	public void findByIdClubNotFound() throws Exception {
		when(clubRestService.findById(anyLong())).thenThrow(new EntityNotFoundException("message"));

		mvc.perform(get(PATH_CLUBS_ID, 1L))
				.andExpect(status().isNotFound());

		verify(clubRestService).findById(1L);
		verifyNoMoreInteractions(clubRestService);
	}

	@Test
	public void createIsValid() throws Exception {
		ClubDto club = new ClubDto(0L, "Club 1", "CLUB1", true);

		when(clubRestService.create(any(ClubDto.class))).thenReturn(club);

		mvc.perform(post(PATH_CLUBS)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", club.getName())
				.param("acronym", club.getAcronym())
				.param("enabled", String.valueOf((club.isEnabled()))))
				.andExpect(status().isFound())
				.andExpect(view().name("redirect:/clubs"))
				.andExpect(redirectedUrl("/clubs"));

		ArgumentCaptor<ClubDto> dtoCaptor = ArgumentCaptor.forClass(ClubDto.class);
		verify(clubRestService).create(dtoCaptor.capture());
		verifyNoMoreInteractions(clubRestService);

		ClubDto capturedClub = dtoCaptor.getValue();
		assertThat(capturedClub.getId(), is(club.getId()));
		assertThat(capturedClub.getName(), is(club.getName()));
		assertThat(capturedClub.getAcronym(), is(club.getAcronym()));
		assertThat(capturedClub.isEnabled(), is(club.isEnabled()));
	}

	@Test
	public void createIsInvalid() throws Exception {
		ClubDto club = new ClubDto(0L, "1", "CLB_1", true);

		mvc.perform(post(PATH_CLUBS)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", club.getName())
				.param("acronym", club.getAcronym())
				.param("enabled", String.valueOf((club.isEnabled()))))
				.andExpect(status().isOk())
				.andExpect(view().name(is("club")))
				.andExpect(model().attributeHasFieldErrors("entity", "name"))
				.andExpect(model().attributeHasFieldErrors("entity", "acronym"));

		verifyZeroInteractions(clubRestService);
	}

	@Test
	public void createDuplicatedAcronym() throws Exception {
		ClubDto club = new ClubDto(0L, "Club 1", "CLUB1", true);

		when(clubRestService.create(any(ClubDto.class))).thenThrow(new DuplicatedClubAcronymException(club.getAcronym()));

		mvc.perform(post(PATH_CLUBS)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", String.valueOf(club.getId()))
				.param("name", club.getName())
				.param("acronym", club.getAcronym())
				.param("enabled", String.valueOf((club.isEnabled()))))
				.andExpect(status().isOk())
				.andExpect(view().name(is("club")))
				.andExpect(model().attributeHasFieldErrors("entity", "acronym"));

		verify(clubRestService).create(any(ClubDto.class));
		verifyNoMoreInteractions(clubRestService);
	}

	@Test
	public void createClubNotFound() throws Exception {
		ClubDto club = new ClubDto(0L, "Club 1", "CLUB1", true);

		when(clubRestService.create(any(ClubDto.class))).thenThrow(new EntityNotFoundException("message"));

		mvc.perform(post(PATH_CLUBS)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", String.valueOf(club.getId()))
				.param("name", club.getName())
				.param("acronym", club.getAcronym())
				.param("enabled", String.valueOf((club.isEnabled()))))
				.andExpect(status().isNotFound());

		verify(clubRestService).create(any(ClubDto.class));
		verifyNoMoreInteractions(clubRestService);
	}

	@Test
	public void getNew() throws Exception {
		mvc.perform(get(PATH_CLUBS_NEW))
				.andExpect(status().isOk())
				.andExpect(view().name(is("club")));

		verifyZeroInteractions(clubRestService);
	}

	@Test
	public void updateIsValid() throws Exception {
		ClubDto club = new ClubDto(1L, "Club 1", "CLUB1", true);

		when(clubRestService.update(any(ClubDto.class))).thenReturn(club);

		mvc.perform(post(PATH_CLUBS_ID, club.getId())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", String.valueOf(club.getId()))
				.param("name", club.getName())
				.param("acronym", club.getAcronym())
				.param("enabled", String.valueOf((club.isEnabled()))))
				.andExpect(status().isFound())
				.andExpect(view().name("redirect:/clubs"))
				.andExpect(redirectedUrl("/clubs"));

		ArgumentCaptor<ClubDto> dtoCaptor = ArgumentCaptor.forClass(ClubDto.class);
		verify(clubRestService).update(dtoCaptor.capture());
		verifyNoMoreInteractions(clubRestService);

		ClubDto capturedClub = dtoCaptor.getValue();
		assertThat(capturedClub.getId(), is(club.getId()));
		assertThat(capturedClub.getName(), is(club.getName()));
		assertThat(capturedClub.getAcronym(), is(club.getAcronym()));
		assertThat(capturedClub.isEnabled(), is(club.isEnabled()));
	}

	@Test
	public void updateIsInvalid() throws Exception {
		ClubDto club = new ClubDto(1L, "1", "CLB_1", true);

		mvc.perform(post(PATH_CLUBS_ID, club.getId())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", club.getName())
				.param("acronym", club.getAcronym())
				.param("enabled", String.valueOf((club.isEnabled()))))
				.andExpect(status().isOk())
				.andExpect(view().name(is("club")))
				.andExpect(model().attributeHasFieldErrors("entity", "name"))
				.andExpect(model().attributeHasFieldErrors("entity", "acronym"));

		verifyZeroInteractions(clubRestService);
	}

	@Test
	public void updateDuplicatedAcronym() throws Exception {
		ClubDto club = new ClubDto(1L, "Club 1", "CLUB1", true);

		when(clubRestService.update(any(ClubDto.class))).thenThrow(new DuplicatedClubAcronymException(club.getAcronym()));

		mvc.perform(post(PATH_CLUBS_ID, club.getId())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", String.valueOf(club.getId()))
				.param("name", club.getName())
				.param("acronym", club.getAcronym())
				.param("enabled", String.valueOf((club.isEnabled()))))
				.andExpect(status().isOk())
				.andExpect(view().name(is("club")))
				.andExpect(model().attributeHasFieldErrors("entity", "acronym"));

		verify(clubRestService).update(any(ClubDto.class));
		verifyNoMoreInteractions(clubRestService);
	}

	@Test
	public void updateClubNotFound() throws Exception {
		ClubDto club = new ClubDto(1L, "Club 1", "CLUB1", true);

		when(clubRestService.update(any(ClubDto.class))).thenThrow(new EntityNotFoundException("message"));

		mvc.perform(post(PATH_CLUBS_ID, club.getId())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", String.valueOf(club.getId()))
				.param("name", club.getName())
				.param("acronym", club.getAcronym())
				.param("enabled", String.valueOf((club.isEnabled()))))
				.andExpect(status().isNotFound());

		verify(clubRestService).update(any(ClubDto.class));
		verifyNoMoreInteractions(clubRestService);
	}

	@Test
	public void disableIsValid() throws Exception {
		final ClubDto club = new ClubDto(1L, "Club 1", "CLUB1", false);

		when(clubRestService.disable(club.getId())).thenReturn(club);

		mvc.perform(post(PATH_CLUBS_ID_DISABLE, club.getId())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().isFound())
				.andExpect(view().name("redirect:/clubs"))
				.andExpect(redirectedUrl("/clubs"));

		verify(clubRestService).disable(club.getId());
		verifyNoMoreInteractions(clubRestService);
	}

	@Test
	public void disableClubNotFound() throws Exception {
		ClubDto club = new ClubDto(1L, "Club 1", "CLUB1", true);

		when(clubRestService.disable(club.getId())).thenThrow(new EntityNotFoundException("message"));

		mvc.perform(post(PATH_CLUBS_ID_DISABLE, club.getId())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().isNotFound());

		verify(clubRestService).disable(club.getId());
		verifyNoMoreInteractions(clubRestService);
	}
	
	@Test
	public void enableIsValid() throws Exception {
		final ClubDto club = new ClubDto(1L, "Club 1", "CLUB1", true);

		when(clubRestService.enable(club.getId())).thenReturn(club);

		mvc.perform(post(PATH_CLUBS_ID_ENABLE, club.getId())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().isFound())
				.andExpect(view().name("redirect:/clubs"))
				.andExpect(redirectedUrl("/clubs"));

		verify(clubRestService).enable(club.getId());
		verifyNoMoreInteractions(clubRestService);
	}
	
	@Test
	public void enableClubNotFound() throws Exception {
		ClubDto club = new ClubDto(1L, "Club 1", "CLUB1", true);

		when(clubRestService.enable(club.getId())).thenThrow(new EntityNotFoundException("message"));

		mvc.perform(post(PATH_CLUBS_ID_ENABLE, club.getId())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().isNotFound());

		verify(clubRestService).enable(club.getId());
		verifyNoMoreInteractions(clubRestService);
	}
}
