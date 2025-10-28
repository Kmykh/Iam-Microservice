package upc.edu.muusmart.iamservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import upc.edu.muusmart.iamservice.model.AppUser;
import upc.edu.muusmart.iamservice.repository.UserRepository;

import java.util.stream.Collectors;

/**
 * Implementation of Spring Security's {@link UserDetailsService} backed by the {@link UserRepository}.
 *
 * <p>This service adapts {@link AppUser} entities into Spring Security's {@link UserDetails} objects,
 * which are used by the authentication manager to perform authentication and create a security context.
 * Roles are converted into {@link SimpleGrantedAuthority} instances.</p>
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new User(appUser.getUsername(),
                appUser.getPassword(),
                appUser.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.name()))
                        .collect(Collectors.toSet()));
    }
}