package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.dto.UserDTO;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.projections.UserDetailsProjection;
import com.devsuperior.dscommerce.repositories.UserRepository;
import com.devsuperior.dscommerce.tests.ProductFactory;
import com.devsuperior.dscommerce.tests.UserDetailsFactory;
import com.devsuperior.dscommerce.tests.UserFactory;
import com.devsuperior.dscommerce.util.CustomUserUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService service;
    @Mock
    private UserRepository repository;

    @Mock
    private CustomUserUtil userUtil;

    private String existingUserName, nonExistingUserName;
    private User user;
    private List <UserDetailsProjection> userDetails;


    @BeforeEach
    void setUp() throws Exception {

        existingUserName = "Maria@gmail.com";
        nonExistingUserName = "user@gmail.com";


        user = UserFactory.createCustomClientUser( 1L,existingUserName);
        userDetails = UserDetailsFactory.createCustomClienteUser(existingUserName);




        // findById
        Mockito.when(repository.searchUserAndRolesByEmail(existingUserName)).thenReturn(userDetails);
        Mockito.when(repository.searchUserAndRolesByEmail(nonExistingUserName)).thenReturn(new ArrayList<>());

        //findByEmail
        Mockito.when(repository.findByEmail(existingUserName)).thenReturn(Optional.of(user));
        Mockito.when(repository.findByEmail(nonExistingUserName)).thenReturn(Optional.empty());


    }
    @Test
    public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {

        UserDetails result = service.loadUserByUsername(existingUserName);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getUsername(), existingUserName);
    }

    @Test
    public void loadUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesEcist() {
        Assertions.assertThrows(UsernameNotFoundException.class, ()-> {
            service.loadUserByUsername(nonExistingUserName);
        });

    }

    @Test
    public void authenticatedShouldReturnUserWhenUserExist() {

        Mockito.when(userUtil.getLoggedUsername()).thenReturn(existingUserName);

        User result = service.authenticated();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getUsername(), existingUserName);

    }
    @Test
    public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
        Mockito.doThrow(ClassCastException.class).when(userUtil).getLoggedUsername();

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            service.authenticated();
        });
    }
    @Test
    public void getMeShouldReturnUserDTOWhenUserAuthenticated() {

        UserService spyUserService= Mockito.spy(service);
        Mockito.doReturn(user).when(spyUserService).authenticated();


        UserDTO result = spyUserService.getMe();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getEmail(), existingUserName);

    }
    @Test
    public void getMeShouldThrowUsernameNotFoundExceptionWhenUserNotAuthenticated() {
        UserService spyUserService= Mockito.spy(service);
        Mockito.doThrow(UsernameNotFoundException.class).when(spyUserService).authenticated();

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            @SuppressWarnings("unused")
            UserDTO result = spyUserService.getMe();
        });
    }

}