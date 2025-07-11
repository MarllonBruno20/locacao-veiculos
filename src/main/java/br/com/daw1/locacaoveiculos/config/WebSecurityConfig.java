package br.com.daw1.locacaoveiculos.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class WebSecurityConfig {

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // ---- PARTE 1: FILTRAGEM DE ROTAS ----

                        // Regras para o Administrador:
                        // Qualquer URL que comece com /admin/ só pode ser acessada por quem tem o papel 'ADMINISTRADOR'
                        .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")

                        // Regras para Usuários Logados (Clientes):
                        // A área do cliente e o processo de locação exigem autenticação.
                        .requestMatchers("/locacao/**").authenticated()

                        // Rotas Públicas:
                        // A página inicial, a listagem pública de veículos, e os arquivos estáticos (CSS, JS) são permitidos para todos.
                        .requestMatchers("/", "/veiculos/**", "/login", "/css/**", "/js/**", "/imagens/**").permitAll()

                        // Qualquer outra requisição não listada acima exigirá autenticação.
                        .anyRequest().authenticated()
                )
                // ---- PARTE 2: CONFIGURAÇÃO DO LOGIN ----
                .formLogin(form -> form
                        .loginPage("/login") // Define a URL da sua página de login customizada (opcional)
                        .successHandler(customAuthenticationSuccessHandler)
                        .permitAll() // Permite que todos acessem a página de login
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout") // Para onde vai após o logout
                        .permitAll()
                )
                .rememberMe(withDefaults());

        return http.build();
    }
}
