package com.friendsocial.Backend.config;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.IOException;

/*
When a request comes in it is first processed in this class.
 */

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
          @NonNull HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    final String authHeader = request.getHeader("Authorization");
    final String jwtToken;
    final String userEmail;

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      // If authorization header is missing or doesn't start with "Bearer <Token>"
      filterChain.doFilter(request, response);
      return;
    }

    jwtToken = authHeader.substring(7);

    // the try-catch is necessary to get a 401 ERROR if the token is expired. token is no longer
    // valid, and parsing its claims is not possible, resulting in the exception being thrown.
    try {
      // Check if the token is expired
      if (jwtService.isTokenExpired(jwtToken)) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }

      // Token is not expired, so extract the username from it
      userEmail = jwtService.extractUsername(jwtToken);
      System.out.println("Java searching this email in userDetailsService.loadByUsername: " + userEmail);
      if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        // If user does not have an active authentication token yet...
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
        if (jwtService.isTokenValid(jwtToken, userDetails)) {
          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                  userDetails,
                  null,
                  userDetails.getAuthorities()
          );
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }
      System.out.println("Token: " + jwtToken);
      filterChain.doFilter(request, response);
      System.out.println("Token: " + jwtToken);

    } catch (ExpiredJwtException ex) {
      System.out.println("Token expired: " + ex.getMessage());
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
  }
}
