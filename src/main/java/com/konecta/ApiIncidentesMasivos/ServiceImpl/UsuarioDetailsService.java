package com.konecta.ApiIncidentesMasivos.ServiceImpl;

import com.konecta.ApiIncidentesMasivos.Entity.UserEntity;
import com.konecta.ApiIncidentesMasivos.Repository.UserRepository;
import com.konecta.ApiIncidentesMasivos.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioDetailsService.class);
    private final Utils utilLogs;
    @Value("${application.name}")
    private String applicationName;

    @Autowired
    private UserRepository userRepo;

    public UsuarioDetailsService(Utils utilLogs, UserRepository userRepo) {
        this.utilLogs = utilLogs;
        this.userRepo = userRepo;
    }

    @Override // Validar info usuario
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Optional<UserEntity> userGet = userRepo.findByUserName(username);
            if (userGet.isPresent() && username.equals(userGet.get().getUserName())) {
                    if (userGet.get().getAppName().equals(applicationName)) {
                        return new org.springframework.security.core.userdetails.User(userGet.get().getUserName(), userGet.get().getPassword(), new ArrayList<>());
                    } else {
                        utilLogs.logApiError("Application name incorrect");
                        return null;
                    }

            }
        } catch (Exception e) {
            String message = "User not found with username: " + username + e.getMessage();
            utilLogs.logApiError(message);
            logger.info(String.valueOf(e));
            return null;
        }
        return null;
    }

    // Validar password usuario
    public Boolean validatePassword(String username, String password) throws UsernameNotFoundException {
        boolean respuesta = false;
        Optional<UserEntity> userGet = userRepo.findByUserName(username);
        if (userGet.isPresent()) {
            BCryptPasswordEncoder b = new BCryptPasswordEncoder();

            if (b.matches(password, userGet.get().getPassword())) {
                respuesta = true;
            }
        }
        return respuesta;
    }

}
