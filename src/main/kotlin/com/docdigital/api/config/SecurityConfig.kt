package com.docdigital.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {

        http
            .cors { }
            .csrf { it.disable() }

            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

            .authorizeHttpRequests { auth ->
                auth

                    // cadastro de usuário
                    .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()

                    // autenticação
                    .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/auth/forgot-password").permitAll()
                    .requestMatchers(HttpMethod.POST, "/auth/reset-password").permitAll()
                    .requestMatchers(HttpMethod.POST, "/auth/confirmar-cadastro").permitAll()

                    // acesso público aos arquivos
                    .requestMatchers(HttpMethod.GET, "/documentos/visualizar/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/documentos/download/**").permitAll()

                    // necessário para CORS (preflight)
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                    // resto protegido por JWT
                    .anyRequest().authenticated()
            }

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {

        val configuration = CorsConfiguration()

        // Flutter Web usa portas aleatórias
        configuration.allowedOriginPatterns = listOf("*")

        configuration.allowedMethods = listOf(
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "OPTIONS"
        )

        configuration.allowedHeaders = listOf("*")

        configuration.exposedHeaders = listOf(
            "Authorization",
            "Content-Disposition"
        )

        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)

        return source
    }
}