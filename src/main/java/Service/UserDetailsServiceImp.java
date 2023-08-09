package Service;

import Model.Utilisateur;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImp implements UserDetailsService {

    private UtilisateurService utilisateurService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurService.loadUserByUsername(username);
        if(utilisateur==null) throw new UsernameNotFoundException(String.format("User not found",username));
        String[] roles = utilisateur.getRoles().stream().map(u -> u.getRole()).toArray(String[]::new);
        UserDetails userDetails = User
                .withUsername(utilisateur.getUsername())
                .password(utilisateur.getPassword())
                .roles(roles).build();
        return userDetails;
    }


}
