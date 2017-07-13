package org.giste.club.web.controller;

import javax.validation.Valid;

import org.giste.club.common.dto.CategoryDto;
import org.giste.club.web.service.exception.DuplicatedCategoryNameException;
import org.giste.spring.util.controller.CrudController;
import org.giste.spring.util.service.BaseRestService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for managing categories.
 * 
 * @author Giste
 */
@Controller
@RequestMapping("/categories")
public class CategoryController extends CrudController<CategoryDto> {

	/**
	 * Constructs a new controller for managing categories.
	 * 
	 * @param restService RestService for communicating with REST server.
	 */
	public CategoryController(BaseRestService<CategoryDto> restService) {
		super(restService);
	}

	@Override
	protected CategoryDto getNewDto() {
		return new CategoryDto();
	}

	@Override
	protected String getBaseView() {
		return "category";
	}

	@Override
	protected String getBasePath() {
		return "/categories";
	}

	@Override
	@PostMapping
	public String create(@Valid @ModelAttribute("entity") final CategoryDto category, BindingResult result) {
		try {
			return super.create(category, result);
		} catch (DuplicatedCategoryNameException e) {
			return treatDuplicatedName(category, result);
		}
	}

	@Override
	@PostMapping("/{id}")
	public String update(@PathVariable("id") long id, @Valid @ModelAttribute("entity") final CategoryDto category,
			BindingResult result) {
		try {
			return super.update(id, category, result);
		} catch (DuplicatedCategoryNameException e) {
			return treatDuplicatedName(category, result);
		}
	}

	private String treatDuplicatedName(CategoryDto user, BindingResult result) {
		// Treat this exception as a validation one.
		final String[] params = { user.getName() };
		result.rejectValue("name", "Duplicated.category.name", params, "Duplicated.category.name");

		return "category";
	}
}
