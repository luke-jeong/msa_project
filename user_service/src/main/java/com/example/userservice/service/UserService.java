package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

//webSecurity에서 auth.userDetailsService에 넣기위해서는 UserDetailService를 상속받은 서비스여야함
// 상속 받았으니까 impl에서 구현 필요
public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);

    UserDto getUserByUserId(String userId);

    //가공하지 않고 가져오기만 하는 경우 Entity를 바로 사용해도 된다.
    Iterable<UserEntity> getUserByAll();

    UserDto getUserDetailsByEmail(String userName);
}
