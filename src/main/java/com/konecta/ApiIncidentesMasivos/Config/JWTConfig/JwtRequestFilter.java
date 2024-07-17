package com.konecta.ApiIncidentesMasivos.Config.JWTConfig;


import com.konecta.ApiIncidentesMasivos.DTO.RespuestaApiDto;
import com.konecta.ApiIncidentesMasivos.ServiceImpl.JwtUtilService;
import com.konecta.ApiIncidentesMasivos.ServiceImpl.UsuarioDetailsService;
import com.konecta.ApiIncidentesMasivos.utils.Utils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UsuarioDetailsService userDetailsService;
    private final JwtUtilService jwtUtilService;
    private final Utils utilLogs;

    @Value("${token.secrect.key}")
    private String secretKey;

    public JwtRequestFilter(UsuarioDetailsService userDetailsService, JwtUtilService jwtUtilService, Utils utilLogs) {
        this.userDetailsService = userDetailsService;
        this.jwtUtilService = jwtUtilService;
        this.utilLogs = utilLogs;
    }

    private void exception(HttpServletResponse response, RespuestaApiDto responseError) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json; charset=utf-8");
        ObjectMapper mapper = new ObjectMapper();
        String jsonResponse = mapper.writeValueAsString(responseError);
        response.getWriter().write(jsonResponse);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        String jwtInvalid = "JWT invalid";
        try {
            final String authorizationHeader = request.getHeader("Authorization");

            String username = null;
            String jwt = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                username = jwtUtilService.extractUsername(jwt);
            }
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                String ipToken = getIpToken(authorizationHeader);

                if(ipToken.equals(request.getRemoteAddr())){
                    if (jwtUtilService.validateToken(jwt, userDetails) && ipToken.equals(request.getRemoteAddr())) {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }else{
                    throw new IOException("IPs no coinciden:  Ip token: "+ipToken+" IP cliente: "+request.getRemoteAddr());
                }
            }
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            utilLogs.logApiError("Token expired ---> " + e.getMessage());
            RespuestaApiDto responseError = new RespuestaApiDto(false, "TK-002", jwtInvalid);
            exception(response, responseError);
            logger.error(e);
        } catch (SignatureException e) {
            utilLogs.logApiError("Token error signature ---> " + e.getMessage());
            RespuestaApiDto responseError = new RespuestaApiDto(false, "TK-003", jwtInvalid);
            exception(response, responseError);
            logger.error(e);
        } catch (IOException e) {
            utilLogs.logApiError("Token error ---> " + e.getMessage());
            RespuestaApiDto responseError = new RespuestaApiDto(false, "TK-004", jwtInvalid);
            exception(response, responseError);
            logger.error(e);
        } catch (Exception e) {
            utilLogs.logApiError("Excepcion no controlada en token---> " + e.getMessage() + " ------> Revise el usuario, contraseña o si expiró el token");
            RespuestaApiDto responseError = new RespuestaApiDto(false, "TK-000", "Error validando o generando el token");
            exception(response, responseError);
            logger.error(e);
        }
    }

    private String getIpToken(String authorizationHeader){
        String jwToken = authorizationHeader.substring(7); //Se quita la palabra Bearer del header token
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwToken)
                .getBody();
        return claims.get("IP", String.class);
    }
}