package com.financial_tech_lab.financecontrol.service;

import com.financial_tech_lab.financecontrol.dto.request.UserRequest;
import com.financial_tech_lab.financecontrol.dto.response.UserResponse;
import com.financial_tech_lab.financecontrol.entity.User;
import com.financial_tech_lab.financecontrol.exception.duplicate.DuplicateEmailException;
import com.financial_tech_lab.financecontrol.exception.notfound.UserNotFoundException;
import com.financial_tech_lab.financecontrol.mapper.UserMapper;
import com.financial_tech_lab.financecontrol.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        UserRequest request = new UserRequest("Pedro", "pedro@email.com");

        User entity = User.builder()
                .id(1L)
                .name("Pedro")
                .email("pedro@email.com")
                .createdAt(LocalDateTime.now())
                .build();

        UserResponse response = new UserResponse(1L, "Pedro", "pedro@email.com", entity.getCreatedAt());

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(entity);
        when(userMapper.toResponse(entity)).thenReturn(response);

        UserResponse result = userService.create(request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("pedro@email.com");
        verify(userRepository).existsByEmail("pedro@email.com");
        verify(userRepository).save(entity);
    }

    @Test
    @DisplayName("Should throw DuplicateEmailException when email already exists")
    void shouldThrowWhenEmailAlreadyExists() {
        UserRequest request = new UserRequest("Pedro", "pedro@email.com");

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> userService.create(request));

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return user by id")
    void shouldReturnUserById() {
        User entity = User.builder()
                .id(1L)
                .name("Pedro")
                .email("pedro@email.com")
                .createdAt(LocalDateTime.now())
                .build();

        UserResponse response = new UserResponse(1L, "Pedro", "pedro@email.com", entity.getCreatedAt());

        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(userMapper.toResponse(entity)).thenReturn(response);

        UserResponse result = userService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found")
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(99L));
    }

    @Test
    @DisplayName("Should return all users")
    void shouldReturnAllUsers() {
        User u1 = User.builder().id(1L).name("Pedro").email("p@email.com").build();
        User u2 = User.builder().id(2L).name("Ana").email("a@email.com").build();

        when(userRepository.findAll()).thenReturn(List.of(u1, u2));
        when(userMapper.toResponse(u1)).thenReturn(new UserResponse(1L, "Pedro", "p@email.com", null));
        when(userMapper.toResponse(u2)).thenReturn(new UserResponse(2L, "Ana", "a@email.com", null));

        List<UserResponse> result = userService.findAll();

        assertThat(result).hasSize(2);
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUser() {
        User entity = User.builder().id(1L).name("Pedro").email("pedro@email.com").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));

        userService.delete(1L);

        verify(userRepository).delete(entity);
    }

    @Test
    @DisplayName("Should throw when deleting non-existent user")
    void shouldThrowWhenDeletingNonExistentUser() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.delete(99L));

        verify(userRepository, never()).delete(any());
    }
}
