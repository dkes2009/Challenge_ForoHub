package com.konecta.ApiIncidentesMasivos.Controller;

import com.konecta.ApiIncidentesMasivos.DTO.RespuestaApiDto;
import com.konecta.ApiIncidentesMasivos.DTO.TokenResponseDto;
import com.konecta.ApiIncidentesMasivos.Entity.UserEntity;
import com.konecta.ApiIncidentesMasivos.ServiceImpl.JwtUtilService;
import com.konecta.ApiIncidentesMasivos.ServiceImpl.UsuarioDetailsService;
import com.konecta.ApiIncidentesMasivos.utils.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/")
public class AutenticacionApiController {

    private final UsuarioDetailsService usuarioDetailsService;
    private final JwtUtilService jwtUtilService;
    private final Utils utilLogs;
    private long startTime;
    private long endTime;
    private long totalTime;
    private static int consecutivo;

    public AutenticacionApiController(UsuarioDetailsService usuarioDetailsService, JwtUtilService jwtUtilService, Utils utilLogs) {
        this.usuarioDetailsService = usuarioDetailsService;
        this.jwtUtilService = jwtUtilService;
        this.utilLogs = utilLogs;
    }

    @PostMapping("/publico/authenticate")
    public RespuestaApiDto authenticate(@RequestBody UserEntity authenticationReq, javax.servlet.http.HttpServletRequest request, HttpServletResponse res) {
        consecutivo = utilLogs.consecutivo();
        startTime = System.nanoTime();
        String tramaRespon;
        RespuestaApiDto response = new RespuestaApiDto(false, null, null);
        utilLogs.logApi(0, "Autenticando al usuario " + authenticationReq.getUserName(), request.getRemoteAddr(), consecutivo, "authenticate", null);
        final Boolean validatePassword = usuarioDetailsService.validatePassword(authenticationReq.getUserName(), authenticationReq.getPassword());
        final UserDetails userDetails = usuarioDetailsService.loadUserByUsername(authenticationReq.getUserName());
        if (validatePassword && userDetails != null) {
            final String jwt = jwtUtilService.generateToken(userDetails, request.getRemoteAddr());
            TokenResponseDto tokenInfo = new TokenResponseDto();
            tokenInfo.setJwtToken(jwt);
            response.setSuccess(true);
            response.setCodigo("000");
            response.setMessage("Token created successfully");
            response.setData(tokenInfo);
            tramaRespon = response.getMessage() ;
        } else {
            res.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setCodigo("TK-001");
            response.setMessage("Incorrect user or password");
            tramaRespon = response.getCodigo() + " " + response.getCodigo() + " username: " + authenticationReq.getUserName();
            utilLogs.logApiError(tramaRespon);
        }
        endTime = System.nanoTime();
        totalTime = (endTime - startTime) / 1000000;
        utilLogs.logApi(1, tramaRespon+ " Demoro: "+ totalTime,request.getRemoteAddr(), consecutivo, "authenticate", null);
        return response;
    }
}
