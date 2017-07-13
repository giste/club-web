package org.giste.club.web.controller;

import java.util.EnumMap;
import java.util.Map;

import javax.validation.Valid;

import org.giste.club.common.dto.Role;
import org.giste.club.common.dto.UserDto;
import org.giste.club.web.service.exception.DuplicatedUserMailException;
import org.giste.spring.util.controller.CrudController;
import org.giste.spring.util.locale.LocaleMessage;
import org.giste.spring.util.service.BaseRestService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for user actions.
 * 
 * @author Giste
 */
@Controller
@RequestMapping("/users")
public class UserController extends CrudController<UserDto> {

	private LocaleMessage localeMessage;

	public UserController(BaseRestService<UserDto> restService, LocaleMessage localeMessage) {
		super(restService);
		this.localeMessage = localeMessage;
	}

	@Override
	protected UserDto getNewDto() {
		return new UserDto();
	}

	@Override
	protected String getBaseView() {
		return "user";
	}

	@Override
	protected String getBasePath() {
		return "/users";
	}

	@Override
	@PostMapping
	public String create(@Valid @ModelAttribute("entity") final UserDto user, BindingResult result) {
		try {
			return super.create(user, result);
		} catch (DuplicatedUserMailException e) {
			return treatDuplicatedEmail(user, result);
		}
	}

	@Override
	@PostMapping("/{id}")
	public String update(@PathVariable("id") long id, @Valid @ModelAttribute("entity") final UserDto user,
			BindingResult result) {
		try {
			return super.update(id, user, result);
		} catch (DuplicatedUserMailException e) {
			return treatDuplicatedEmail(user, result);
		}
	}

	private String treatDuplicatedEmail(UserDto user, BindingResult result) {
		// Treat this exception as a validation one.
		final String[] params = { user.getEmail() };
		result.rejectValue("email", "Duplicated.user.email", params, "Duplicated.user.email");

		return "user";
	}

	@ModelAttribute("roles")
	public Map<Role, String> getRoles() {
		Map<Role, String> roleMap = new EnumMap<Role, String>(Role.class);
		for (Role role : Role.values()) {
			roleMap.put(role, localeMessage.getMessage("role." + role.toString().toLowerCase()));
		}

		return roleMap;
	}
}
