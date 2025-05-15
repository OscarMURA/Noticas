package co.icesi.taskManager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import co.icesi.taskManager.utils.JwtFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class AppConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(t -> t.disable())
            .csrf(c -> c.disable())
            .authorizeHttpRequests(requests -> requests
                    .requestMatchers("/login").permitAll()
                    .requestMatchers(HttpMethod.GET, "/tasks/**").hasAnyAuthority("VIEW_TASK")
                    .requestMatchers(HttpMethod.POST,"/tasks/**").hasAnyAuthority("CREATE_TASK")
                    .requestMatchers(HttpMethod.PUT,"/tasks/**").hasAnyAuthority("UPDATE_TASK")
                    .requestMatchers(HttpMethod.DELETE,"/tasks/**").hasAnyAuthority("DELETE_TASK")
                    .anyRequest().authenticated())
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .sessionManagement(t -> t.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

            
        return http.build();
    }
}
