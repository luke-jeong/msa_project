package com.example.userservice.security;

import com.example.userservice.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    //yml 파일에서 데이터 받아오기위해 Environment도 추가
    private Environment env;

    //매개변수로 들어온 값 주입
    public WebSecurity(Environment env, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.env = env;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Override
    //권한에 관련된 configure override
    protected void configure(HttpSecurity http) throws Exception{
        http.csrf().disable();
//        http.authorizeRequests().antMatchers("/users/**").permitAll();
        http.authorizeRequests().antMatchers("/**")
                .access("hasIpAddress('192.168.1.100') or hasIpAddress('127.0.0.1')")
                .and()
                .addFilter(getAuthenticationFilter());
        //화면 프레임 나뉘는 부분 disable
        http.headers().frameOptions().disable();
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        authenticationFilter.setAuthenticationManager(authenticationManager());

        return authenticationFilter;
    }

    @Override
    //인증과 관련된 configure override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //사용자 조회 -> 비밀번호 encrypt
        //userService는 바로 들어가는게 아니라 구현된 service가 들어가야함
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }
}
