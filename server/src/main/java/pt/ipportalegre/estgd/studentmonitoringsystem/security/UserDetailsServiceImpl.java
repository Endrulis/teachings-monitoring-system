package pt.ipportalegre.estgd.studentmonitoringsystem.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.MyUser;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.Role;
import pt.ipportalegre.estgd.studentmonitoringsystem.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        MyUser user = userRepository.findByEmail(username);

        if(user == null)
            throw new UsernameNotFoundException("User not found with username: " + username);

        System.out.println("Loaded user: " + username);
        System.out.println("User ID: " + user.getId());
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Password: " + user.getPassword());
        System.out.println("Role ID: " + user.getRole().getId() + ", Role Name: " + user.getRole().getName());

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getName()));

        return mapUserToCustomUserDetails(user, authorities);
/*
        return new User(
                username,
                user.getPassword(),
                true,
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority(user.getRole().getName()))
        );*/
    }

    private CustomUserDetails mapUserToCustomUserDetails(MyUser user, List<SimpleGrantedAuthority> authorities) {
        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setId(user.getId());
        customUserDetails.setUsername(user.getEmail());
        customUserDetails.setPassword(user.getPassword());
        customUserDetails.setName(user.getUsername());
        customUserDetails.setAuthorities(authorities);
        return customUserDetails;
    }
}
