package Security;

import Service.UserDetailsServiceImp;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
public class SecurityConfig  {





    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    private UserDetailsServiceImp userDetailsService;

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .addFilterBefore(new LogoutOnLoginFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new LogoutOnLoginFilter(), DigestAuthenticationFilter.class)
                .formLogin()
                .loginPage("/login")  // Specify the URL of your custom login page
                .successHandler(customAuthenticationSuccessHandler)
                .permitAll()
                .and()
                .rememberMe()
                .and()
                .logout()  // Add this section for logout
                .logoutUrl("/logout")  // Specify the logout URL
                .logoutSuccessUrl("/login")  // Redirect to login page after successful logout
                .permitAll()
                .and()
                .authorizeRequests()
                .antMatchers("/assets/**", "/images/**", "/img/**", "/img_clients/**", "/json/**", "/maps/**", "/style.css","/auth/login/**")
                .permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasRole("USER")
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling()  // Handle exceptions, including access denied
                .accessDeniedPage("/denied")  // Specify the access denied page URL
                .and()
                .userDetailsService(userDetailsService);
        return httpSecurity.build();
    }

    private static class LogoutOnLoginFilter implements Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            if (httpServletRequest.getRequestURI().equals("/login")) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated()) {
                    LogoutHandler logoutHandler = new SecurityContextLogoutHandler();
                    logoutHandler.logout(httpServletRequest, null, authentication);
                }
            }

            chain.doFilter(request, response);
        }
    }






}
