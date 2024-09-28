package dev.gooiman.server.security.application;

import dev.gooiman.server.page.application.PageService;
import dev.gooiman.server.page.repository.entity.Page;
import dev.gooiman.server.security.application.dto.JwtResponseDto;
import dev.gooiman.server.security.application.dto.LoginRequestDto;
import dev.gooiman.server.security.provider.CustomAuthenticationProvider;
import dev.gooiman.server.security.repository.entity.CustomUserDetails;
import dev.gooiman.server.user.repository.UserRepository;
import dev.gooiman.server.user.repository.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomAuthenticationService {

    private final JwtAuthenticationService jwtAuthenticationService;
    private final CustomAuthenticationProvider authenticationProvider;
    private final PageService pageService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public JwtResponseDto login(UUID pageId, LoginRequestDto dto) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            dto.name(), dto.password());

        Optional<Authentication> token = authenticationProvider.authenticate(pageId,
            authentication);

        Authentication authenticatedToken = token.orElseGet(
            () -> signup(pageId, dto.name(), dto.password()));
        String jwtToken = jwtAuthenticationService.createToken(authenticatedToken);

        return new JwtResponseDto(jwtToken);
    }

    public Authentication signup(UUID pageId, String name, String password) {
        String encodedPassword = passwordEncoder.encode(password);

        Page page = pageService.getPageById(pageId);
        UUID userId = UUID.randomUUID();
        User user = new User(userId, name, encodedPassword, "ROLE_USER", page);
        User saveUser = userRepository.save(user);

        return new UsernamePasswordAuthenticationToken(new CustomUserDetails(saveUser), null,
            List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
