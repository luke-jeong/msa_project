package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{
    UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder;

    //Autowired 시 해당 서비스가 구동되면 자동으로 bean에 넣어주지만
    //BCryptPasswordEncoder passwordEncoder 같은 경우, 초기화 시켜주는 구간이 없기 때문에 최초 실행되는
    //UserServiceApplication에 등록해야한다.
    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        //생성 시 id는 랜덤하게 생성되도록 설정
        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));
        //같은 데이터가 들어와도 다른 값이 들어간다.
        userRepository.save(userEntity);

        UserDto returnUserDto = mapper.map(userEntity, UserDto.class);
        return returnUserDto;
    }
}
