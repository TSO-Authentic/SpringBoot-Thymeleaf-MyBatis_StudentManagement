package com.studentmanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.studentmanagement.dao.ClassRepository;
import com.studentmanagement.dto.ClassRequestDto;
import com.studentmanagement.dto.ClassResponseDto;
import com.studentmanagement.model.ClassBean;


@Controller
public class ClassController {

	@Autowired
	private ClassRepository classRepository;
	
	@GetMapping(value="/setupClass")
	public ModelAndView setupClass() {
		return new ModelAndView("BUD003", "cBean", new ClassBean());
	}
	
	@PostMapping(value="/addClass")
	public String addClass(@ModelAttribute("cBean") @Validated ClassBean classBean, BindingResult br,ModelMap model) {
		if(br.hasErrors()) {
			return "BUD003";
		}
		
		ClassRequestDto cDTO = new ClassRequestDto();
		cDTO.setId(classBean.getId());
		
		
		List<ClassResponseDto> checkClassList = classRepository.findById(cDTO);
		
		if(checkClassList.size() != 0) {
			model.addAttribute("Error", "Class ID has been already used.... Choose another class ID");
		}else {
			cDTO.setName(classBean.getName());
			int i = classRepository.insert(cDTO);
			
			if( i > 0) {
				model.addAttribute("Success", "Class registered Successfully");
			}else {
				model.addAttribute("Error", "Class registered fail !!!");
			}
		}
		return "BUD003";
	}
}
