package org.giste.club.web.controller;

import javax.validation.Valid;

import org.giste.club.common.dto.ClubDto;
import org.giste.club.web.service.ClubRestService;
import org.giste.club.web.service.exception.DuplicatedClubAcronymException;
import org.giste.spring.util.controller.CrudeController;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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
public class ClubController extends CrudeController<ClubDto> {

	/**
	 * Construct the controller with a service used to perform actions on a REST
	 * server.
	 * 
	 * @param clubRestService Service used to communicate with REST server.
	 */
	public ClubController(ClubRestService clubRestService) {
		super(clubRestService);
	}

	private String treatDuplicatedAcronym(ClubDto club, BindingResult result) {
		// Treat this exception as a validation one.
		final String[] params = { club.getAcronym() };
		result.rejectValue("acronym", "Duplicated.club.acronym", params, "Duplicated.club.acronym");

		return "club";
	}

	@Override
	protected ClubDto getNewDto() {
		return new ClubDto();
	}

	@Override
	protected String getBaseView() {
		return "club";
	}

	@Override
	@PostMapping
	public String create(@Valid @ModelAttribute("entity") final ClubDto club, BindingResult result) {
		try {
			return super.create(club, result);
		} catch (DuplicatedClubAcronymException e) {
			return treatDuplicatedAcronym(club, result);
		}
	}

	@Override
	@PostMapping("/{id}")
	public String update(@PathVariable("id") long id, @Valid @ModelAttribute("entity") final ClubDto club,
			BindingResult result) {
		try {
			return super.update(id, club, result);
		} catch (DuplicatedClubAcronymException e) {
			return treatDuplicatedAcronym(club, result);
		}
	}

	@Override
	protected String getBasePath() {
		return "/clubs";
	}

}
