package Controller;

import Model.Utilisateur;
import Service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;


@Controller
public class AdminController implements ErrorController {

    @Autowired
    private UtilisateurService utilisateurService;

    @ModelAttribute
    public void addCommonUserAttributes(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            Utilisateur utilisateur = utilisateurService.loadUserByUsername(username);

            if (utilisateur != null) {
                model.addAttribute("username", username);
                model.addAttribute("fullName", utilisateur.getFullName());
                model.addAttribute("profession", utilisateur.getProfession());
                model.addAttribute("imagePath", utilisateur.getCheminImage());
                // You can add more attributes here
            }
        }
    }

    @GetMapping("/")
        public String getIndex(Model model){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            // Extract the username from the authentication object
            String username = authentication.getName();
            // Assuming you have a method to load a user by username
            Utilisateur utilisateur = utilisateurService.loadUserByUsername(username);
            model.addAttribute("activePage","index");
            return "admin/index";
        }

        @GetMapping("/profil")
        public String getProfil(Model model){
            model.addAttribute("activePage", "profil");
            return "admin/profil";
        }



    @GetMapping("/login")
    public String getReg(Model model) {
        return "admin/connexion";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "redirect:/login";
    }

    @GetMapping("/denied")
    public String Denied(){
        return "denied";
    }




}
