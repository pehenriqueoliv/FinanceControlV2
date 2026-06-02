package com.financial_tech_lab.financecontrol.mapper;

import com.financial_tech_lab.financecontrol.dto.request.UserRequest;
import com.financial_tech_lab.financecontrol.dto.response.UserResponse;
import com.financial_tech_lab.financecontrol.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        return User.builder()
                .name(request.name())
                .email(request.email())
                .build();
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
