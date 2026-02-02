package com.project.bankwebapp.Services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.imageio.plugins.tiff.GeoTIFFTagSet;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private JwtService jwtService;
    public JwtInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("Interceptor caught request: " + request.getRequestURI());
        // 1. Allow pre-flight checks from the browser (CORS)
        if (request.getMethod().equals("OPTIONS")) return true;

        // we get the header then check that bearer exists
        // we get the token by removing the "bearer " from the header
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401); // Unauthorized
            return false; // Stop the request here
        }


        final String token = authHeader.substring(7);


        if (jwtService.validateToken(token)) {


            String userIdFromToken = jwtService.extractUserId(token);

            request.setAttribute("authenticatedUserId", userIdFromToken);

            return true;
        }

        response.setStatus(401);
        return false;
    }
}



