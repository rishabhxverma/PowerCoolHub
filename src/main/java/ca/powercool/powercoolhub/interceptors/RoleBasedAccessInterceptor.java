package ca.powercool.powercoolhub.interceptors;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.models.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class RoleBasedAccessInterceptor implements HandlerInterceptor {

    @SuppressWarnings("null")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        String path = request.getRequestURI();

        boolean managerExclusivePath = path.startsWith("/manager") || path.startsWith("/users/manager") || path.startsWith("/customers");

        boolean technicianExclusivePath = path.startsWith("/technician") || path.startsWith("/users/technician");

        if (managerExclusivePath && (user == null || !user.getRole().equals(UserRole.MANAGER))) {
            response.sendRedirect("/login");
            return false; // Prevent further processing of the request
        }

        else if (technicianExclusivePath && (user == null || !user.getRole().equals(UserRole.TECHNICIAN))) {
            response.sendRedirect("/login");
            return false; // Prevent further processing of the request
        }

        return true; // Allow the request to proceed
    }
}
