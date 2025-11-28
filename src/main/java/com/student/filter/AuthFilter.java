package com.student.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Authentication Filter - Checks if user is logged in
 * Protects all pages except login and public resources
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/*"})
public class AuthFilter implements Filter {
    
    // Public URLs that don't require authentication
    private static final String[] PUBLIC_URLS = {
        "/login",
        "/logout",
        ".css",
        ".js",
        ".png",
        ".jpg",
        ".jpeg",
        ".gif"
    };
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("AuthFilter initialized");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());
        
        // Check if this is a public URL
        if (isPublicUrl(path)) {
            // Allow access to public URLs
            chain.doFilter(request, response);
            return;
        }
        
        // Check if user is logged in
        HttpSession session = httpRequest.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);
        
        if (isLoggedIn) {
            // User is logged in, allow access
            chain.doFilter(request, response);
        } else {
            // User not logged in, redirect to login
            String loginURL = contextPath + "/login";
            httpResponse.sendRedirect(loginURL);
        }
    }
    
    @Override
    public void destroy() {
        System.out.println("AuthFilter destroyed");
    }
    
    /**
     * Check if URL is public (doesn't require authentication)
     */
    private boolean isPublicUrl(String path) {
        for (String publicUrl : PUBLIC_URLS) {
            if (path.contains(publicUrl)) {
                return true;
            }
        }
        return false;
    }
}
