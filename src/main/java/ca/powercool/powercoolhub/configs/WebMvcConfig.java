package ca.powercool.powercoolhub.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ca.powercool.powercoolhub.converters.StringToDateConverter;
import ca.powercool.powercoolhub.interceptors.RoleBasedAccessInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final RoleBasedAccessInterceptor roleAccessInterceptor;

    @Autowired
    public WebMvcConfig(RoleBasedAccessInterceptor roleAccessInterceptor) {
        this.roleAccessInterceptor = roleAccessInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(roleAccessInterceptor)
                .addPathPatterns("/users/manager/**", "/technician/**", "/customers/**");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToDateConverter());
    }
}
