//package com.example.hanghaefinal.security;
//
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
//public class CustomCorsFilter implements Filter {
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//
//        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//
//
//        httpServletResponse.setHeader("Access-Control-Allow-Origin","https://main.d2l6bnge3hnh7g.amplifyapp.com");
////        httpServletResponse.setHeader("Access-Control-Allow-Origin","http://localhost:3000");
//        httpServletResponse.setHeader("Access-Control-Allow-Methods","*");
//        httpServletResponse.setHeader("Access-Control-Max-Age","3600");
//        httpServletResponse.setHeader("Access-Control-Allow-Headers","*");
//        httpServletResponse.setHeader("Access-Control-Allow-Credentials","true");
//        httpServletResponse.setHeader("Access-Control-Expose-Headers","*");
//
//        if("OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())){
//            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
//        }else{
//            chain.doFilter(request,response);
//        }
//    }
//
//    @Override
//    public void destroy() {
//
//    }
//}
//
