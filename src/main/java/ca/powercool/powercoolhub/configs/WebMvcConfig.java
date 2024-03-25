package ca.powercool.powercoolhub.configs;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

import ca.powercool.powercoolhub.converters.StringToDateConverter;
import ca.powercool.powercoolhub.interceptors.RoleBasedAccessInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final RoleBasedAccessInterceptor roleAccessInterceptor;

    public WebMvcConfig(RoleBasedAccessInterceptor roleAccessInterceptor) {
        this.roleAccessInterceptor = roleAccessInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(roleAccessInterceptor)
                .addPathPatterns("/users/manager/**", "/manager/**", "/users/technician/**", "/technician/**", "/customers/**");

        registry.addInterceptor(new WebContentInterceptor() {
            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
                if (!response.isCommitted()) {
                    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                    response.setHeader("Pragma", "no-cache");
                    response.setHeader("Expires", "0");
                }
                super.postHandle(request, response, handler, modelAndView);
            }
        });
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToDateConverter());
    }

    
}
