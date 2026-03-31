package com.devsuperior.dscommerce.tests;

import com.devsuperior.dscommerce.projections.UserDetailsProjection;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsFactory {

    public static List <UserDetailsProjection> createCustomClienteUser(String username) {
        List<UserDetailsProjection> list = new ArrayList<>();
        list.add(new UserDetailImpl(username, "123",1L,"ROLE_CLIENT"));
        return list;


    }
    public static List <UserDetailsProjection> createCustomAdminUser(String username) {
        List<UserDetailsProjection> list = new ArrayList<>();
        list.add(new UserDetailImpl(username, "123", 2L, "ROLE_ADMIN"));
        return list;
    }
    public static List <UserDetailsProjection> createCustomAdmicLIENTnUser(String username) {
        List<UserDetailsProjection> list = new ArrayList<>();
        list.add(new UserDetailImpl(username, "123", 1L, "ROLE_CLIENT"));
        list.add(new UserDetailImpl(username, "123", 2L, "ROLE_ADMIN"));
        return list;
    }


        static class UserDetailImpl implements UserDetailsProjection {

    private String username;
    private String password;
    private Long roleId;
    private String authority;

    public void UserDetailsImp() {

    }

    public UserDetailImpl(String username, String password, Long roleId, String authority) {
        this.username = username;
        this.password = password;
        this.roleId = roleId;
        this.authority = authority;
    }

    public UserDetailImpl() {
        super();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Long getRoleId() {
        return roleId;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

}
}
