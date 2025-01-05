package pt.ipportalegre.estgd.studentmonitoringsystem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.enabled;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = false,prePostEnabled = true,securedEnabled = true)
public class WebSecurityConfig {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((requests) -> requests
                        /*.requestMatchers("/api/v1/users")
                        .hasAuthority("ADMIN")*/
                        .requestMatchers("/",
                                "/api/v1/hello",
                                "/api/v1/auth/signup",
                                "/api/v1/auth/authenticate",
                                "/api/v1/attendance/**",
                                "/api/v1/classes/**",
                                "/api/v1/curricular-units/**",
                                "/api/v1/participation-facts/**",
                                "/api/v1/users/**",
                                "/api/v1/roles",
                                "/api/v1/participation-scores/**",
                                "/api/v1/categories/**"
                        )
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form.loginPage("/login")
                        .permitAll())
                .logout((logout) -> logout.permitAll())
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
