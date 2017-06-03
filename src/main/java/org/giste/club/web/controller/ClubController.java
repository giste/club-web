package org.giste.club.web.controller;

import java.util.List;

import javax.validation.Valid;

import org.giste.club.common.dto.ClubDto;
import org.giste.club.web.service.ClubRestService;
import org.giste.club.web.service.exception.DuplicatedClubAcronymException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for club actions.
 * 
 * @author Giste
 */
@Controller
@RequestMapping("/clubs")
public class ClubController {

	private ClubRestService clubRestService;

	public ClubController(ClubRestService clubRestService) {
		this.clubRestService = clubRestService;
	}

	@GetMapping
	public String findAll(Model model) {

		List<ClubDto> clubList = clubRestService.findAll();
		model.addAttribute("clubList", clubList);

		return "clubs";
	}

	@GetMapping("/{id}")
	public String findById(@PathVariable("id") long id, Model model) {
		ClubDto club = clubRestService.findById(id);
		model.addAttribute("club", club);

		return "club";
	}

	@PostMapping
	public String create(@Valid @ModelAttribute("club") final ClubDto club, BindingResult result) {
		if (result.hasErrors()) {
			return "club";
		}

		try {
			clubRestService.create(club);
		} catch (DuplicatedClubAcronymException e) {
			return treatDuplicatedAcronym(club, result);
		}

		return "redirect:/clubs";
	}

	@GetMapping("/new")
	public String getNew(Model model) {
		ClubDto club = new ClubDto();
		model.addAttribute("club", club);

		return ("club");
	}

	@PostMapping("/{id}")
	public String update(@PathVariable("id") long id, @Valid @ModelAttribute("club") final ClubDto club,
			BindingResult result) {
		if (result.hasErrors()) {
			return "club";
		}

		try {
			clubRestService.update(club);
		} catch (DuplicatedClubAcronymException e) {
			return treatDuplicatedAcronym(club, result);
		}

		return "redirect:/clubs";
	}

	@PostMapping("/{id}/disable")
	public String disable(@PathVariable("id") long id) {
		clubRestService.disable(id);

		return "redirect:/clubs";
	}

	@PostMapping("/{id}/enable")
	public String enable(@PathVariable("id") long id) {
		clubRestService.enable(id);

		return "redirect:/clubs";
	}

	private String treatDuplicatedAcronym(ClubDto club, BindingResult result) {
		// Treat this exception as a validation one.
		final String[] params = { club.getAcronym() };
		result.rejectValue("acronym", "Duplicated.club.acronym", params, "Duplicated.club.acronym");
		
		return "club";
	}
}
