package com.studentmanagement.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

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

import com.studentmanagement.dao.UserRepository;
import com.studentmanagement.dto.UserRequestDto;
import com.studentmanagement.dto.UserResponseDto;
import com.studentmanagement.model.LoginBean;
import com.studentmanagement.model.SearchBean;
import com.studentmanagement.model.UserBean;

@Controller
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/")
	public ModelAndView login() {

		return new ModelAndView("LGN001", "loginBean", new LoginBean());
	}

	@PostMapping("/login")
	public String login(@ModelAttribute("loginBean") @Validated LoginBean loginBean, BindingResult br, ModelMap model,
			HttpSession session) {

		if (br.hasErrors()) {
			return "LGN001";
		}

		UserRequestDto uDTO = new UserRequestDto();
		uDTO.setId(loginBean.getId());
		List<UserResponseDto> userList = userRepository.findAll();

		if (userList.size() == 0) {
			model.addAttribute("Error", "User ID not found !!!");
			return "LGN001";
		} else {
			if (userList.get(0).getPassword().equals(loginBean.getPassword())) {
				session.setAttribute("userId", userList.get(0).getId());
				session.setAttribute("userName", userList.get(0).getName());

				return "M00001";
			} else {
				model.addAttribute("Error", "Incorrect Password .... Try Again !!!");
				return "LGN001";
			}
		}
	}

	@GetMapping(value = "/welcome")
	public String mainPage() {
		return "M00001";
	}

	@GetMapping(value = "/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}

	@GetMapping("/setupUser")
	public ModelAndView setupUser(@ModelAttribute("Error") String Error,@ModelAttribute("Success")String Success, ModelMap model) {
		model.addAttribute("Error", Error);
		model.addAttribute("Success",Success);
		return new ModelAndView("USR001", "sBean", new SearchBean());
	}

	@GetMapping(value = "/searchUser")
	public String searchUser(@ModelAttribute("sBean") SearchBean searchBean, ModelMap model) {

		List<UserResponseDto> userList = new ArrayList<>();
		UserRequestDto uDTO = new UserRequestDto();

		uDTO.setId(searchBean.getUserId());
		uDTO.setName(searchBean.getUserName());

		if(!uDTO.getId().equals("") || !uDTO.getName().equals("")) {
			 userList = userRepository.findByFilter(uDTO);
		}else {
			 userList = userRepository.findAll();
		}
		
		if (userList.size() == 0) {
			model.addAttribute("Error", "No User Found !!!");
		} else {
			model.addAttribute("userList", userList);
			model.addAttribute("Success", "Search done Successfully");
		}
		return "USR001";
	}

	@GetMapping(value = "/setupAddUser")
	public ModelAndView addUser() {

		return new ModelAndView("USR002", "uBean", new UserBean());
	}

	@PostMapping(value = "/addUser")
	public String addUser(@ModelAttribute("uBean") @Validated UserBean userBean, BindingResult br, ModelMap model) {
		if (br.hasErrors()) {
			return "USR002";
		}
		UserRequestDto uDTO = new UserRequestDto();

		if (userBean.getPassword().equals(userBean.getConfirm())) {

			uDTO.setId(userBean.getId());
			

			List<UserResponseDto> checkUserList = userRepository.findByFilter(uDTO);

			if (checkUserList.size() != 0) {
				model.addAttribute("Error", "User ID has been already used..... Choose another user ID");
			} else {
				uDTO.setName(userBean.getName());
				uDTO.setPassword(userBean.getPassword());
				int i = userRepository.insert(uDTO);

				if (i > 0) {
					model.addAttribute("Success", "User registered Successfully");
				} else {
					model.addAttribute("Error", "User register fail !!!");
				}
			}
		} else {
			model.addAttribute("Error", "Passwords didn't match !!!");
		}
		return "USR002";
	}
	
	@GetMapping(value="setupUpdateUser")
	public ModelAndView setupUpdateUser(@RequestParam String id) {
		UserRequestDto uDTO = new UserRequestDto();
		uDTO.setId(id);
		
		List<UserResponseDto> list = userRepository.findByFilter(uDTO);
		UserBean userBean = new UserBean();
		for (UserResponseDto upDTO : list) {
			userBean.setId(upDTO.getId());
			userBean.setName(upDTO.getName());
			userBean.setPassword(upDTO.getPassword());
			userBean.setConfirm(upDTO.getPassword());
		}
		return new ModelAndView("USR002-01", "uBean", userBean);
	}
	
	@PostMapping(value="updateUser")
	public String updateUser(@ModelAttribute("uBean") @Validated UserBean userBean, BindingResult br, ModelMap model) {
		if (br.hasErrors()) {
			return "USR002-01";
		}

		UserRequestDto uDTO = new UserRequestDto();
		if (userBean.getPassword().equals(userBean.getConfirm())) {
			uDTO.setId(userBean.getId());
			uDTO.setName(userBean.getName());
			uDTO.setPassword(userBean.getPassword());

			int i = userRepository.update(uDTO);

			if (i > 0) {
				model.addAttribute("Success", "User Updated Successfully");
			}
		} else {
			model.addAttribute("Error", "Password didn't match !!!");
		}
		return "USR002-01";
		
	}
	
	
	@GetMapping(value="/deleteUser")
	public String deleteUser(@RequestParam String id, RedirectAttributes redir, ModelMap model, HttpSession session) {
		UserRequestDto uDTO = new UserRequestDto();
		uDTO.setId(id);

		if (uDTO.getId().equals(session.getAttribute("userId"))) {
			redir.addAttribute("Error", "Cann't delete this current login user !!!");
		} else {

			int i = userRepository.deleteById(uDTO);

			if (i > 0) {
				redir.addAttribute("Success", "Deleted " + uDTO.getId() + " Successfully");
			}
			
		}
		return "redirect:/setupUser";
	}
}
