package com.hotel.interceptor;

import com.hotel.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT拦截器
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    private static final ThreadLocal<Integer> currentUserId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentUsername = new ThreadLocal<>();

    public static Integer getCurrentUserId() {
        return currentUserId.get();
    }

    public static String getCurrentUsername() {
        return currentUsername.get();
    }

    public static void clear() {
        currentUserId.remove();
        currentUsername.remove();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取token
        String token = request.getHeader("Authorization");

        // 检查token是否存在且以"Bearer "开头
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉"Bearer "前缀

            // 验证token
            if (jwtUtil.validateToken(token)) {
                // 从token中提取用户信息
                Integer userId = jwtUtil.getUserIdFromToken(token);
                String username = jwtUtil.getUsernameFromToken(token);

                // 将用户信息存储到ThreadLocal中
                currentUserId.set(userId);
                currentUsername.set(username);

                return true;
            }
        }

        // token无效，返回401未授权
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"未授权或token已过期\",\"data\":null}");
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理ThreadLocal
        clear();
    }
}
