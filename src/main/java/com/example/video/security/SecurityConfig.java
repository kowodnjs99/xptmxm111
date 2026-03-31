package com.example.video.security;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.util.Collection;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .httpBasic(AbstractHttpConfigurer::disable);

        http
                .csrf(AbstractHttpConfigurer::disable);
//                .csrf(csrf -> csrf
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                );

        http
                .authorizeHttpRequests((authorize) -> authorize
//                                .requestMatchers("/**").permitAll()
//                                .requestMatchers("/").permitAll()
                                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                                .requestMatchers("/member/login", "/member/chuga","/member/chugaProc").permitAll()
                                .requestMatchers("/board/**", "/error/**","/index/**").permitAll()
                                .requestMatchers("/post/**", "/video/**").permitAll()
                                .requestMatchers("/dataRoom/**").authenticated()
//                                .requestMatchers("/admin/**").hasRole("ADMIN")
//                                .requestMatchers("/member/list").hasRole("ADMIN")
//                                .anyRequest().authenticated() // 인증이 필요함


                                .requestMatchers(HttpMethod.POST, "/member/login").permitAll()
                                .requestMatchers("/member/login").permitAll()
                                .anyRequest().permitAll()
                );

        http
                .formLogin(form -> form
                        .loginPage("/member/login")
                        .loginProcessingUrl("/member/login")
                        .defaultSuccessUrl("/board/dualList")
                        .failureUrl("/error/error")
                        .permitAll()
                );

        http
                .logout(logout -> logout
                        .logoutUrl("/member/logout")
                        .logoutSuccessUrl("/board/dualList")
                        .invalidateHttpSession(true)
                );

        return http.build();
    }


    //    //import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//    //import org.springframework.security.crypto.password.PasswordEncoder;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
