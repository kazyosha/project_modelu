package com.c04.productmodule.services;

import com.c04.productmodule.dto.user.RoleDTO;
import com.c04.productmodule.models.Role;
import com.c04.productmodule.repositories.IRoleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {

    private IRoleRepository roleRepository;
    public RoleService(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    public List<RoleDTO> getAllRoles(){
        List<Role> roles = roleRepository.findAll();
        List<RoleDTO> list = new ArrayList<>();
        for (Role role : roles) {
            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setId(role.getId());
            roleDTO.setName(role.getName());
            list.add(roleDTO);
        }
        return list;
    }
}
