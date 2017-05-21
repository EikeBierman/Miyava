package com.miyava.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration
    extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure( HttpSecurity http )
        throws Exception {
        http
            .authorizeRequests()
            .antMatchers( "/plugins/**", "/bootstrap/**", "/css/**", "/js/**", "/fonts/**", "/img/**", "/", "/home" ).permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .loginPage( "/login" )
            .defaultSuccessUrl( "/index" )
            .permitAll()
            .and()
            .logout()
            .permitAll();
        http.exceptionHandling().accessDeniedPage( "/403" );

        // // TODO: configure the security settings
        // http.authorizeRequests().anyRequest().permitAll();
        // http.csrf().disable();
    }

    @Autowired
    public void configureGlobal( AuthenticationManagerBuilder auth )
        throws Exception {
        auth.inMemoryAuthentication().withUser( "user" ).password( "123456" ).roles( "ADMIN" );

        auth.jdbcAuthentication()
            .dataSource( dataSource )
            .passwordEncoder( new BCryptPasswordEncoder() )
            .usersByUsernameQuery(
                "select username,password, enabled from user where username=?" )
            .authoritiesByUsernameQuery(
                "select u.username, ur.user_roles from user_roles ur, user u where ur.id = u.id and u.username=?" );

    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
