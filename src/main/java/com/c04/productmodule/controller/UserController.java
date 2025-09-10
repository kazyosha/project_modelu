package com.c04.productmodule.controller;

import com.c04.productmodule.dto.user.CreateUserDTO;
import com.c04.productmodule.dto.user.EditUserDTO;
import com.c04.productmodule.dto.user.RoleDTO;
import com.c04.productmodule.dto.user.UserDTO;
import com.c04.productmodule.repositories.response.ListUserResponse;
import com.c04.productmodule.services.RoleService;
import com.c04.productmodule.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class UserController {
    private final UserService userService;
    private final RoleService roleService;
    private final HttpSession httpSession;

    public UserController(UserService userService, RoleService roleService, HttpSession httpSession) {
        this.userService = userService;
        this.roleService = roleService;
        this.httpSession = httpSession;
    }

    @GetMapping
    public String listUsers(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam Map<String, String> params,

            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        httpSession.setAttribute("email", email);

        int size = 5;
        page = Math.max(page, 1);
        int zeroBasedPage = page - 1;


        ListUserResponse listUserResponse = fetchUsersByFilter(keyword, zeroBasedPage, size);

        params.remove("page");
        String queryString = buildQueryString(params);
        
        model.addAttribute("queryParams", queryString);
        model.addAttribute("totalPages", listUserResponse.getTotalPage());
        model.addAttribute("currentPage", page);
        model.addAttribute("users", listUserResponse.getUsers());
        model.addAttribute("keyword", keyword);

        return "admin/list-user";
    }

    private Long parseDepartmentId(String departmentIdStr) {
        if (departmentIdStr != null && !departmentIdStr.isBlank()) {
            try {
                return Long.parseLong(departmentIdStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private ListUserResponse fetchUsersByFilter(String keyword, int page, int size) {
        if (keyword != null && !keyword.isBlank()) {
            return userService.searchUsersByName(keyword, page, size);
        } else {
            return userService.getAllUsers(page, size);
        }
    }

    private String buildQueryString(Map<String, String> params) {
        return params.entrySet().stream()
                .filter(e -> e.getValue() != null && !e.getValue().isBlank())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
    }

    @GetMapping("/user/create")
    public String createUser(Model model) {
        CreateUserDTO createUserDTO = new CreateUserDTO();
        List<RoleDTO> roles = roleService.getAllRoles();
        model.addAttribute("roles", roles);
        model.addAttribute("user", createUserDTO);

        return "admin/create-user";
    }

    @GetMapping("/user/{id}/detail")
    public String userDetail(@PathVariable("id") int id,
                             Model model) {
        UserDTO user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/admin";
        }
        model.addAttribute("user", user);
        return "admin/detail-user";
    }

    @GetMapping("/user/{id}/delete")
    public String deleteUser(@PathVariable("id") int id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }

    @PostMapping("/user/create")
    public String storeUser(@Valid @ModelAttribute("user") CreateUserDTO createUserDTO,
                            BindingResult result, Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleService.getAllRoles());
            return "admin/create-user";
        }
        userService.storeUser(createUserDTO);
        return "redirect:/admin";
    }

    @GetMapping("/user/{id}/edit")
    public String showFormEdit(@Valid @PathVariable("id") int id, Model model) {
        UserDTO user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/admin";
        }

        EditUserDTO editUserDTO = new EditUserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone()
        );
        editUserDTO.setDepartmentId(user.getDepartmentId());
        editUserDTO.setRoleId(user.getRoleId());

        List<RoleDTO> roles = roleService.getAllRoles();
        model.addAttribute("user", editUserDTO);
        model.addAttribute("roles", roles);

        return "admin/edit-user";
    }

    @PostMapping("/user/{id}/edit")
    public String updateUser(@PathVariable("id") int id,
                             @Valid @ModelAttribute("user") EditUserDTO editUserDTO,
                             BindingResult result, Model model) throws IOException {
        UserDTO user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/admin";
        }
        if (result.hasErrors()) {
            List<RoleDTO> roles = roleService.getAllRoles();
            model.addAttribute("roles", roles);
            return "admin/edit-user";
        }
        userService.updateUser(id, editUserDTO);
        return "redirect:/admin";
    }
}
