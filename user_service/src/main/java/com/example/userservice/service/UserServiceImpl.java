package com.example.userservice.service;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.UserDto;
import com.example.userservice.error.FeignErrorDecoder;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
// import com.example.userservice.vo.ResponseOrder;
import com.example.userservice.vo.ResponseOrder;
import com.example.userservice.vo.ResponseUser;
import feign.FeignException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Data
@Slf4j
public class UserServiceImpl implements UserService{
    UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder;

    Environment env;
    RestTemplate restTemplate;

    OrderServiceClient orderServiceClient;
    FeignErrorDecoder feignErrorDecoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);

        //사용자가 존재하지 않는 경우
        if (userEntity == null) {
            throw new UsernameNotFoundException(username);
        }
        //리턴되는 User는 UserDetails에 들어있는 User
        return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(),
                true, true, true, true,
                new ArrayList<>());
    }

    //Autowired 시 해당 서비스가 구동되면 자동으로 bean에 넣어주지만
    //BCryptPasswordEncoder passwordEncoder 같은 경우, 초기화 시켜주는 구간이 없기 때문에 최초 실행되는
    //UserServiceApplication에 등록해야한다.
    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           Environment env,
                           RestTemplate restTemplate,
                           OrderServiceClient orderServiceClient,
                           FeignErrorDecoder feignErrorDecoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
        this.restTemplate = restTemplate;
        this.orderServiceClient = orderServiceClient;
        this.feignErrorDecoder = feignErrorDecoder;
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
    //Override는 이미 등록 되어있는 것을 재정의 하는것. 구현하지 않아도 오류발생 안한다.
    //implement는 인터페이스에 있는 것을 가져온 것이기에 재정의를 해줘야한다.


    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if(userEntity == null){
            throw new UsernameNotFoundException("User not found");
        }
        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
//        List<ResponseOrder> orders = new ArrayList<>();

        //rest template 사용, 주소는 변경가능하도록 user-service.yml에 적용. %s는 String.format을 이용해 변수로 넣어줌
        //feign 쓰게되면서 주석처리
//        String orderUrl = String.format(env.getProperty("order_service.url"),userId);
//        ResponseEntity<List<ResponseOrder>> orderListResponse =
//                restTemplate.exchange(orderUrl, HttpMethod.GET, null,
//                                        new ParameterizedTypeReference<List<ResponseOrder>>() {
//        });
        // order service에서 받아온 entity 타입을 변환
//        List<ResponseOrder> orderList = orderListResponse.getBody();

        /* Feign */
//        List<ResponseOrder> orderList = orderServiceClient.getOrders(

        /* Feign Exception Handling */
//        List<ResponseOrder> orderList = null;
//        try{
//            orderList = orderServiceClient.getOrders(userId);
//        }catch(FeignException e){
//            log.error(e.getMessage());
//        }


        /* Error Decoder */
        List<ResponseOrder> orderList = orderServiceClient.getOrders(userId);
        userDto.setOrders(orderList);
        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }
        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
        return userDto;
    }
}
