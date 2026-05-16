package com.lin101.store.interceptor;

import com.lin101.store.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 鉴权：校验请求头 {@code Authorization} 的 Bearer Token，合法则在 {@code request} 上设置 {@code userId} 属性。
 * 与 {@link com.lin101.store.config.WebConfig} 中配置的拦截路径一致。
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * OPTIONS 预检直接放行；鉴权失败返回 HTTP 401 与 JSON 提示。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Integer userId = jwtUtils.getUserIdFromToken(token);

            if (userId != null) {
                request.setAttribute("userId", userId);
                return true;
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\": 401, \"message\": \"未授权或登录已过期，请重新登录\"}");
        return false;
    }
}