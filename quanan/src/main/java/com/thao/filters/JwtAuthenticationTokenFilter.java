/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.thao.filters;

import com.thao.components.JwtService;
import com.thao.pojo.NguoiDung;
import com.thao.service.NguoiDungService;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

/**
 *
 * @author Chung Vu
 */
public class JwtAuthenticationTokenFilter extends UsernamePasswordAuthenticationFilter {

    private final static String TOKEN_HEADER = "Authorization";
    @Autowired
    private JwtService jwtService;
    @Autowired
    private NguoiDungService userService;

//    public JwtAuthenticationTokenFilter(JwtService jwtService, NguoiDungService userService, AuthenticationManager authenticationManager) {
//        super(authenticationManager);
//        this.jwtService = jwtService;
//        this.userService = userService;
//
//        this.setRequiresAuthenticationRequestMatcher(new OrRequestMatcher(
//                new AntPathRequestMatcher("/login/**", "POST")
////                 new AntPathRequestMatcher("/user/register")
//        ));
//    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authToken = httpRequest.getHeader(TOKEN_HEADER);
        System.out.println(httpRequest.getHeaderNames());
        System.out.println(response.getContentType());
        if (jwtService.validateTokenLogin(authToken)) {
            String username = jwtService.getUsernameFromToken(authToken);
            NguoiDung user = userService.getNguoiDungByUsername(username);
            if (user != null) {
                boolean enabled = true;
                boolean accountNonExpired = true;
                boolean credentialsNonExpired = true;
                boolean accountNonLocked = true;

                Set<GrantedAuthority> authorities = new HashSet<>();
                authorities.add(new SimpleGrantedAuthority(user.getVaiTro()));

                UserDetails userDetail = new org.springframework.security.core.userdetails.User(username, user.getMatKhau(), enabled, accountNonExpired,
                        credentialsNonExpired, accountNonLocked, authorities);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail,
                        null, userDetail.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }
}
