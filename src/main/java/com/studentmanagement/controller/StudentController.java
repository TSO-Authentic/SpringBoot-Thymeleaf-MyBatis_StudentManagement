package com.studentmanagement.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.studentmanagement.dao.ClassRepository;
import com.studentmanagement.dao.StudentRepository;
import com.studentmanagement.dto.ClassResponseDto;
import com.studentmanagement.dto.StudentRequestDto;
import com.studentmanagement.dto.StudentResponseDto;
import com.studentmanagement.model.SearchBean;
import com.studentmanagement.model.StudentBean;

@Controller
public class StudentController {

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private ClassRepository classRepository;

	@GetMapping(value = "/setupStudentSearch")
	public ModelAndView setupStudentSearch(@ModelAttribute("Success") String Success, ModelMap model) {
		model.addAttribute("Success", Success);

		return new ModelAndView("BUD001", "sStuBean", new SearchBean());
	}

	@GetMapping(value = "/searchStudent")
	public String searchStudent(@ModelAttribute("sStuBean") SearchBean searchBean, ModelMap model) {

		List<StudentResponseDto> studentList = new ArrayList<>();
		StudentRequestDto sDTO = new StudentRequestDto();

		sDTO.setStudentId(searchBean.getStuId());
		sDTO.setStudentName(searchBean.getStuName());
		sDTO.setClassName(searchBean.getClassName());

		if (!sDTO.getStudentId().equals("") || !sDTO.getStudentName().equals("") || !sDTO.getClassName().equals("")) {
			studentList = studentRepository.findByFilter(sDTO);
		} else {
			studentList = studentRepository.findAll();
		}

		if (studentList.size() == 0) {
			model.addAttribute("Error", "No Student Found !!!");
		} else {
			model.addAttribute("studentList", studentList);
			model.addAttribute("Success", "Search done Successfully");
		}
		return "BUD001";
	}

	@GetMapping(value = "/setupStudentAdd")
	public ModelAndView setupAddStudent() {

		return new ModelAndView("BUD002", "stuBean", new StudentBean());
	}

	@PostMapping(value = "/addStudent")
	public String addStudent(@ModelAttribute("stuBean") @Validated StudentBean studentBean, BindingResult br,
			ModelMap model) {

		boolean condition1 = br.hasErrors();
		boolean condition2 = (studentBean.getYear().equals("Year") || studentBean.getMonth().equals("Month")
				|| studentBean.getDay().equals("Day"));
		if (condition1) {
			return "BUD002";
		} else if (condition2) {
			
			model.addAttribute("ErrorDate", "RegisterDate can't be blank!");
			return "BUD002";
			
		} else {

			StudentRequestDto sDTO = new StudentRequestDto();
			sDTO.setStudentId(studentBean.getId());

			List<StudentResponseDto> checkStudentList = studentRepository.findByFilter(sDTO);

			if (checkStudentList.size() != 0) {
				model.addAttribute("Error", "Student ID has been already exist ... Choose another Student ID");
			} else {
				sDTO.setStudentName(studentBean.getName());
				sDTO.setClassName(studentBean.getClassName());
				sDTO.setRegisterDate(studentBean.getYear() + "-" + studentBean.getMonth() + "-" + studentBean.getDay());
				sDTO.setStatus(studentBean.getStatus());
				int i = studentRepository.insert(sDTO);
				if (i > 0) {
					model.addAttribute("Remain", studentBean);
					model.addAttribute("Success", "Student registered Successfully");
				}
			}
		}
		return "BUD002";
	}

	@GetMapping(value = "/setupUpdateStudent")
	public ModelAndView setupUpdateStudent(@RequestParam("studentId") String studentId, ModelMap model) {
		StudentRequestDto dto = new StudentRequestDto();
		dto.setStudentId(studentId);

		List<StudentResponseDto> list = studentRepository.findByFilter(dto);
		StudentBean studentBean = new StudentBean();
		for (StudentResponseDto res : list) {
			studentBean.setId(res.getStudentId());
			studentBean.setName(res.getStudentName());
			studentBean.setClassName(res.getClassName());
			studentBean.setStatus(res.getStatus());
			String[] date = res.getRegisterDate().toString().split("-");
			studentBean.setYear(date[0]);
			studentBean.setMonth(date[1]);
			studentBean.setDay(date[2]);

		}
		return new ModelAndView("BUD002-01", "stuBean", studentBean);
	}

	@PostMapping(value = "/studentUpdate")
	public String updateStudent(@ModelAttribute("stuBean") @Validated StudentBean studentBean, BindingResult br,
			ModelMap model) {

		boolean condition1 = br.hasErrors();
		boolean condition2 = (studentBean.getYear().equals("Year") || studentBean.getMonth().equals("Month")
				|| studentBean.getDay().equals("Day"));
		if (condition1) {
			return "BUD002-01";
		} else if (condition2) {

			model.addAttribute("ErrorDate", "RegisterDate can't be blank!");
			return "BUD002-01";

		} else {

			StudentRequestDto sDTO = new StudentRequestDto();
			sDTO.setStudentId(studentBean.getId());
			sDTO.setStudentName(studentBean.getName());
			sDTO.setClassName(studentBean.getClassName());
			sDTO.setRegisterDate(studentBean.getYear() + "-" + studentBean.getMonth() + "-" + studentBean.getDay());
			sDTO.setStatus(studentBean.getStatus());

			int i = studentRepository.update(sDTO);
			if (i > 0) {
				model.addAttribute("Success", "Student successfully updated");
			}
		}
		return "BUD002-01";
	}

	@GetMapping(value = "/deleteStudent")
	public String deleteStudent(@RequestParam String id, ModelMap model, RedirectAttributes redir) {
		StudentRequestDto sDTO = new StudentRequestDto();
		sDTO.setStudentId(id);

		int i = studentRepository.deleteById(sDTO);
		if (i > 0) {
			redir.addAttribute("Success", "Deleted " + sDTO.getStudentId() + " Successfully");
		}

		return "redirect:/setupStudentSearch";
	}

	@ModelAttribute("cList")
	public List<String> classList() {

		List<ClassResponseDto> classList = classRepository.findAll();
		List<String> clList = new ArrayList<>();

		for (int i = 0; i < classList.size(); i++) {
			clList.add(classList.get(i).getName());
		}

		return clList;
	}
}
