package com.leyou.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration //cors soluciona el problema de Cross-Origin
public class LeyouCorsConfiguration {
    @Bean
    public CorsFilter corsFilter() {
        //Iniciar el objeto de configuración
        CorsConfiguration configuration = new CorsConfiguration();
        //si se necesita llevar cookie, la ruta no puede ser *
        configuration.addAllowedOrigin("http://manage.leyou.com");
        configuration.addAllowedOrigin("http://www.leyou.com");
        configuration.setAllowCredentials(true); //deja llevar cookie
        configuration.addAllowedMethod("*"); //para todos lo métodos：get post put delete
        configuration.addAllowedHeader("*");//se puede llevar todos los tipos de Header
        //filtra todas las rutas
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**", configuration);
        //return instance of corsFilter
        return new CorsFilter(configurationSource);
    }
}
