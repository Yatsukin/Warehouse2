package ru.example.demo.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.example.demo.model.SectionModel;
import ru.example.demo.model.UserModel;
import ru.example.demo.service.sectionModel.SectionServiceImpl;
import ru.example.demo.userDetailsImplementation.UserServiceImpl;
import ru.example.demo.util.UserValidator;

@Controller
public class GreetingController {

	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private SectionServiceImpl sectionService;
	@Autowired
	private UserValidator userValidator;
	
	@GetMapping("/")
	public String greeting(Model model) {
				
		return "greeting";
	}
	
	@GetMapping("/section")
	public String section(@ModelAttribute(name = "add")SectionModel sectionModel,Model model) {
		return getSection(sectionModel, model, 1, "section","unsorted");
	}
	
	@GetMapping("/section/{pageNumber}")
	public String getSection(@ModelAttribute(name = "add")SectionModel sectionModel,Model model,
			@PathVariable("pageNumber") int currentPage,
			@RequestParam(required = false, name = "sortField") String sortField,
			@RequestParam(required = false, name = "sortDirection")String sortDirection) {
		
		
		Page<SectionModel> page = sectionService.findAllSectionPages(currentPage,sortField,sortDirection );
		
		long totalItems = page.getTotalElements();
		long totalPages = page.getTotalPages();
		
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalItems", totalItems);
		model.addAttribute("totalPages", totalPages); 
		
		model.addAttribute("sections", page.getContent());	
		
		
		model.addAttribute("sortField", sortField);	
		model.addAttribute("sortDirection", sortDirection);	
		model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");	
		
		return "section";
	}
	
	@GetMapping("/section/{id}/update")
	public String getUpdateSectionPage(@PathVariable("id")long id,Model model) {
		
		if(sectionService.isExist(id)) {
			model.addAttribute("update",sectionService.findBySectionId(id));
			return "sectionUpdate";
		}else return "redirect:/section";
		
		
		
	}
	@PatchMapping("/section/{id}/update")
	public String updateSection(@ModelAttribute(name = "update")SectionModel sectionModel) {
		
		sectionService.updateProduct(sectionModel);
		
		return "redirect:/section";
	}
	
	@PostMapping("/section/add")
	public String addSection(@ModelAttribute(name = "addcategory")SectionModel sectionModel) {
		
		sectionService.addSection(sectionModel);
		
		return "redirect:/section";
	}
	
	@DeleteMapping("/section/{id}/delete")
	public String deleteSection(@PathVariable("id")long id) {
		
		sectionService.deleteSection(id);
		
		return "redirect:/section";
	}	
		
	@GetMapping("/login")
	public String getLoginPage(@ModelAttribute(name="user") UserModel authorizationModel) {
		
		return "login";
	}
	
	@GetMapping("/registration")
	public String getRegestrationPage(@ModelAttribute(name = "registration")UserModel user) {
		
		return "registration";
	}
	
	@PostMapping("/registration")
	public String registration(@Valid @ModelAttribute("registration")UserModel user,BindingResult br,
			Model model) {

		userValidator.validate(user, br);		
		if(br.hasErrors()) return "registration";
		
		userService.saveWithDefaultRole(user);
	
		return"redirect:/login";
	}
	
	
}
