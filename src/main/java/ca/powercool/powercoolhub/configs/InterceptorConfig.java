package ca.powercool.powercoolhub.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.powercool.powercoolhub.interceptors.RoleBasedAccessInterceptor;

@Configuration
public class InterceptorConfig {

    @Bean
    public RoleBasedAccessInterceptor roleAccessInterceptor() {
        return new RoleBasedAccessInterceptor();
    }
}
