package antifraud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@EnableWebSecurity
public class WebSecurityConfigurerImpl extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService) // user store 1
                .passwordEncoder(getEncoder());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint()) // Handles auth error
                .and()
                .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
                .and()
                .authorizeRequests() // manage access
                .antMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                .antMatchers("/actuator/shutdown").permitAll() // needs to run test
                // other matchers
                .mvcMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasAuthority("ROLE_MERCHANT")
                .mvcMatchers(HttpMethod.GET, "/api/auth/list").hasAnyAuthority(
                        "ROLE_ADMINISTRATOR",
                        "ROLE_SUPPORT"
                )
                .mvcMatchers(HttpMethod.DELETE, "/api/auth/user/**").hasAuthority("ROLE_ADMINISTRATOR")
                .mvcMatchers(HttpMethod.PUT, "/api/auth/access").hasAuthority("ROLE_ADMINISTRATOR")
                .mvcMatchers(HttpMethod.PUT, "/api/auth/role").hasAuthority("ROLE_ADMINISTRATOR")
                .mvcMatchers(HttpMethod.POST, "/api/antifraud/suspicious-ip").hasAuthority("ROLE_SUPPORT")
                .mvcMatchers(HttpMethod.DELETE, "/api/antifraud/suspicious-ip/**").hasAuthority("ROLE_SUPPORT")
                .mvcMatchers(HttpMethod.GET, "/api/antifraud/suspicious-ip").hasAuthority("ROLE_SUPPORT")
                .mvcMatchers(HttpMethod.POST, "/api/antifraud/stolencard").hasAuthority("ROLE_SUPPORT")
                .mvcMatchers(HttpMethod.DELETE, "/api/antifraud/stolencard").hasAuthority("ROLE_SUPPORT")
                .mvcMatchers(HttpMethod.GET, "/api/antifraud/stolencard").hasAuthority("ROLE_SUPPORT")
                .mvcMatchers(HttpMethod.GET, "/api/antifraud/history/**").hasAuthority("ROLE_SUPPORT")
                .mvcMatchers(HttpMethod.PUT, "/api/antifraud/transaction").hasAuthority("ROLE_SUPPORT")
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}