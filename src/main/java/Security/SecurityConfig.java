package Security;

import Service.UserDetailsServiceImp;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
                .antMatchers("/assets/**", "/images/**", "/img/**", "/img_clients/**", "/json/**", "/maps/**", "/style.css")
                .permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling()  // Handle exceptions, including access denied
                .accessDeniedPage("/denied")  // Specify the access denied page URL
                .and()
                .userDetailsService(userDetailsService);

        return httpSecurity.build();
    }

}
