package com.c04.productmodule.services;

import com.c04.productmodule.dto.user.CreateUserDTO;
import com.c04.productmodule.dto.user.EditUserDTO;
import com.c04.productmodule.dto.user.UserDTO;
import com.c04.productmodule.models.Role;
import com.c04.productmodule.models.User;
import com.c04.productmodule.repositories.IRoleRepository;
import com.c04.productmodule.repositories.IUserRepository;
import com.c04.productmodule.repositories.response.ListUserResponse;
import com.c04.productmodule.utils.FileManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final String uploadDir = "F:/uploads/";
    protected final IUserRepository userRepository;
    private final FileManager fileManager;
    private final IRoleRepository roleRepository;

    public UserService(IUserRepository userRepository, FileManager fileManager, IRoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.fileManager = fileManager;
        this.roleRepository = roleRepository;
    }

    private String getRoleNames(User user) {
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            return user.getRoles()
                    .stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(", "));
        }
        return "No Role";
    }

    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId().intValue());
        dto.setUsername(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setImageUrl(user.getImageUrl());
        dto.setRoleName(Collections.singletonList(getRoleNames(user)));
        return dto;
    }

    public ListUserResponse getAllUsers(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").ascending());
        Page<User> data = userRepository.findAll(pageable);

        List<UserDTO> userDTOs = data.getContent()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        ListUserResponse response = new ListUserResponse();
        response.setTotalPage(data.getTotalPages());
        response.setCurrentPage(data.getNumber() + 1);
        response.setUsers(userDTOs);

        return response;
    }

    public void deleteById(int id) {
        Optional<User> userOpt = userRepository.findById((long) id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            fileManager.deleteFile(uploadDir + "/" + user.getImageUrl());
            userRepository.delete(user);
        } else {
            throw new RuntimeException("User not found with id: " + id);
        }
    }

    public void storeUser(CreateUserDTO createUserDTO) throws IOException {
        User newUser = new User();
        newUser.setName(createUserDTO.getUsername());
        newUser.setEmail(createUserDTO.getEmail());
        newUser.setPassword(createUserDTO.getPassword());
        newUser.setPhone(createUserDTO.getPhone());

        // Upload ảnh
        MultipartFile file = createUserDTO.getImage();
        if (!file.isEmpty()) {
            String fileName = fileManager.uploadFile(uploadDir, file);
            newUser.setImageUrl(fileName);
        }

        // Gán Roles (n-n)
        if (createUserDTO.getRoleId() != null && !createUserDTO.getRoleId().isEmpty()) {
            List<Role> roles = roleRepository.findAllById(createUserDTO.getRoleId());
            newUser.setRoles(roles);
        }

        userRepository.save(newUser);
    }

    public UserDTO getUserById(int id) {
        Optional<User> userOpt = userRepository.findById((long) id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            UserDTO dto = new UserDTO();
            dto.setId(user.getId().intValue());
            dto.setUsername(user.getName());
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getPhone());
            dto.setImageUrl(user.getImageUrl());

            // ap roles
            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                List<Long> roleIds = user.getRoles()
                        .stream()
                        .map(Role::getId)
                        .toList();
                dto.setRoleId(roleIds);

                // nếu cần cả tên
                String roleNames = user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.joining(", "));
                dto.setRoleName(Collections.singletonList(roleNames));
            }

            return dto;
        }
        return null;
    }

    public void updateUser(int id, EditUserDTO editUserDTO) throws IOException {
        Optional<User> userOpt = userRepository.findById((long) id);
        if (userOpt.isPresent()) {
            User currentUser = userOpt.get();
            currentUser.setName(editUserDTO.getUsername());
            currentUser.setEmail(editUserDTO.getEmail());
            currentUser.setPhone(editUserDTO.getPhone());

            // Cập nhật roles (n-n)
            if (editUserDTO.getRoleId() != null && !editUserDTO.getRoleId().isEmpty()) {
                List<Role> roles = roleRepository.findAllById(editUserDTO.getRoleId());
                currentUser.setRoles(roles);
            }

            // Cập nhật ảnh
            MultipartFile file = editUserDTO.getImage();
            if (file != null && !file.isEmpty()) {
                fileManager.deleteFile(uploadDir + "/" + currentUser.getImageUrl());
                String fileName = fileManager.uploadFile(uploadDir, file);
                currentUser.setImageUrl(fileName);
            }

            userRepository.save(currentUser);
        }
    }

    public ListUserResponse searchUsersByName(String keyword, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").ascending());
        Page<User> data = userRepository.findByNameContainingIgnoreCase(keyword, pageable);

        List<UserDTO> userDTOs = data.getContent()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        ListUserResponse response = new ListUserResponse();
        response.setTotalPage(data.getTotalPages());
        response.setCurrentPage(data.getNumber() + 1);
        response.setUsers(userDTOs);

        return response;
    }

}
