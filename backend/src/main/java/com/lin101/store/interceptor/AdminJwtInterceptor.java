package com.lin101.store.interceptor;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.lin101.store.utils.JwtUtils;

@Component
public class AdminJwtInterceptor implements HandlerInterceptor {

    public static final String ATTR_ADMIN_ID = "adminId";
    public static final String ATTR_ADMIN_ROLE = "adminRole";
    public static final String ATTR_ADMIN_STORE_ID = "adminStoreId";

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            Claims claims = jwtUtils.parseClaims(authHeader.substring(7));
            if (claims != null && "admin".equals(String.valueOf(claims.get("tokenType")))) {
                Integer adminId = parseInteger(claims.getSubject());
                String role = claims.get("role", String.class);
                Integer storeId = parseInteger(claims.get("storeId"));
                if (adminId != null && role != null) {
                    request.setAttribute(ATTR_ADMIN_ID, adminId);
                    request.setAttribute(ATTR_ADMIN_ROLE, role);
                    request.setAttribute(ATTR_ADMIN_STORE_ID, storeId);
                    return true;
                }
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\": 401, \"message\": \"管理端登录已过期，请重新登录\"}");
        return false;
    }

    private Integer parseInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
