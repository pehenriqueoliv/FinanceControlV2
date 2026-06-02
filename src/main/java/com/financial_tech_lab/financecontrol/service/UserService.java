package com.financial_tech_lab.financecontrol.service;

import com.financial_tech_lab.financecontrol.dto.request.UserRequest;
import com.financial_tech_lab.financecontrol.dto.response.UserResponse;
import com.financial_tech_lab.financecontrol.entity.User;
import com.financial_tech_lab.financecontrol.exception.duplicate.DuplicateEmailException;
import com.financial_tech_lab.financecontrol.exception.notfound.UserNotFoundException;
import com.financial_tech_lab.financecontrol.mapper.UserMapper;
import com.financial_tech_lab.financecontrol.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }

        User user = userMapper.toEntity(request);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User findEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    public void delete(Long id) {
        User user = findEntityById(id);
        userRepository.delete(user);
    }
}
