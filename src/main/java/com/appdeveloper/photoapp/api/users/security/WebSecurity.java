package com.appdeveloper.photoapp.api.users.security;

import com.appdeveloper.photoapp.api.users.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
@EnableMethodSecurity(prePostEnabled = true)
@Configuration
@EnableWebSecurity
public class WebSecurity {
    private Environment environment;
    private UsersService usersService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public WebSecurity(Environment environment, UsersService usersService,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.environment = environment;
        this.usersService = usersService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


//    @Bean
//    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
//
//
//
//        http.csrf((csrf) -> csrf.disable());
//
//
//        http
//
//                .authorizeHttpRequests((requests) -> {
//
//                    requests
//
//                            .requestMatchers(new AntPathRequestMatcher("/users"
//
//                                   ,HttpMethod.POST.name())).permitAll()
//
//
//                            .anyRequest().permitAll();
//
//                });
//
//
//        http.sessionManagement((session) ->
//                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//
//        http.headers((headers) ->
//                headers.frameOptions((frameOptions) -> frameOptions.sameOrigin()));
//
//
//        return http.build();
//
//    }


//    @Bean
//    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
//
//        // Configure AuthenticationManagerBuilder
//        AuthenticationManagerBuilder authenticationManagerBuilder = http
//                .getSharedObject(AuthenticationManagerBuilder.class);
//       authenticationManagerBuilder.userDetailsService(usersService).passwordEncoder(bCryptPasswordEncoder);
//
//        // Get AuthenticationManager
//        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
//
//        http
//                .cors(cors -> {})
//                .csrf((csrf) -> csrf.disable())
//                .authorizeHttpRequests((authz) ->
//                        authz.requestMatchers(HttpMethod.POST, "/users")
//                        .access(new WebExpressionAuthorizationManager("hasIpAddress('" + environment.getProperty("gateway.ip") + "')"))
//                        .requestMatchers(HttpMethod.POST, environment.getProperty("login.url.path")).permitAll()
//                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
//                        .anyRequest()
//                        .authenticated())
//             //   .addFilter(getAuthenticationFilter(authenticationManager))
//             //   .addFilter(new AuthorizationFilter(authenticationManager, environment))
//                .addFilter(new AuthenticationFilter(usersService,environment,authenticationManager))
//                .authenticationManager(authenticationManager)
//                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        http.headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.sameOrigin()));
//
//        return http.build();
//    }


//    @Bean
//    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
//
//        http.csrf((csrf) -> csrf.disable());
//
//        http.authorizeHttpRequests((authz) -> authz
//                        .requestMatchers(new AntPathRequestMatcher("/users/**")).access(
//                                new WebExpressionAuthorizationManager("hasIpAddress('"+environment.getProperty("gateway.ip")+"')"))
//                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
//                        .requestMatchers(HttpMethod.POST, "/users/login").permitAll())
//                .sessionManagement((session) -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        http.headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.sameOrigin()));
//
//        return http.build();
//
//    }
//


    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {

        // Configure AuthenticationManagerBuilder
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.userDetailsService(usersService)
                .passwordEncoder(bCryptPasswordEncoder);

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        // Create AuthenticationFilter
        AuthenticationFilter authenticationFilter =
                new AuthenticationFilter(usersService, environment, authenticationManager);
        authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));

        http.csrf((csrf) -> csrf.disable());

        http.authorizeHttpRequests((authz) -> authz
                        .requestMatchers(new AntPathRequestMatcher("/users", "POST")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/users/**")).access(
                        new WebExpressionAuthorizationManager("hasIpAddress('"+environment.getProperty("gateway.ip")+"')"))
                        .requestMatchers(new AntPathRequestMatcher("/users/status/check", "GET")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/users/login")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/actuator/**", "GET")).permitAll())
                        // .requestMatchers(new AntPathRequestMatcher("/users/**","GET,PUT,DELETE")).permitAll()
                       // .requestMatchers(new AntPathRequestMatcher("/users/**", "GET")).permitAll()
                      //  .requestMatchers(new AntPathRequestMatcher("/users/**", "PUT")).permitAll()
                     //   .requestMatchers(new AntPathRequestMatcher("/users/**", "DELETE")).permitAll())
                .addFilter(new AuthorizationFilter(authenticationManager,environment))
              //  .addFilter(authenticationFilter)
                .addFilter(authenticationFilter)
                .authenticationManager(authenticationManager)
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.sameOrigin()));
        return http.build();

    }


}
