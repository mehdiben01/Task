package Controller;

import Model.Utilisateur;
import Repository.UtilisateurRepository;
import Service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;


@Controller
public class AdminController implements ErrorController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private UtilisateurService utilisateurService;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @ModelAttribute
    public void addCommonUserAttributes(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            Utilisateur utilisateur = utilisateurService.loadUserByUsername(username);

            if (utilisateur != null) {
                model.addAttribute("username", username);
                model.addAttribute("fullName", utilisateur.getFullName());
                model.addAttribute("imagePath", utilisateur.getCheminImage());
                model.addAttribute("prenom", utilisateur.getPrenom());
                model.addAttribute("nom", utilisateur.getNom());
                model.addAttribute("profession", utilisateur.getProfession());
                model.addAttribute("tel", utilisateur.getTel());
                model.addAttribute("daten", utilisateur.getDaten());
                model.addAttribute("id", utilisateur.getId());
                model.addAttribute("roles", utilisateur.getRoles());
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
            Utilisateur utilisateur = new Utilisateur();
            model.addAttribute("utilisateur", utilisateur);
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

    @PostMapping("/profil/update")
    public String updateUser(@ModelAttribute("utilisateur") Utilisateur updatedUtilisateur , RedirectAttributes redirectAttributes, BindingResult bindingResult, Model model  ) {

        // Récupérer l'utilisateur existant à partir de la base de données
        Utilisateur existingUtilisateur = utilisateurService.getUtilisateurById(updatedUtilisateur.getId());
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/profil";
        }

        // Vérifier si l'utilisateur existe déjà
        boolean DataExiste = utilisateurRepository.existsByNomAndPrenomAndProfessionAndDatenAndTel(updatedUtilisateur.getNom().toUpperCase(),updatedUtilisateur.getPrenom().toLowerCase(),updatedUtilisateur.getProfession().toLowerCase(),updatedUtilisateur.getDaten(),updatedUtilisateur.getTel());
        if (DataExiste) {
            // Gérer le cas où l'utilisateur existe déjà
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà.");
            return "redirect:/profil";
        }else{
            // Mettre à jour les attributs de l'utilisateur existant avec les nouvelles valeurs
            existingUtilisateur.setNom(updatedUtilisateur.getNom().toUpperCase());
            existingUtilisateur.setPrenom(updatedUtilisateur.getPrenom().toLowerCase());
            existingUtilisateur.setProfession(updatedUtilisateur.getProfession().toLowerCase());
            existingUtilisateur.setDaten(updatedUtilisateur.getDaten());
            existingUtilisateur.setTel(updatedUtilisateur.getTel());

        }
        boolean NomExiste = utilisateurRepository.existsByNomAndPrenomAndIdNot(updatedUtilisateur.getNom().toUpperCase(),updatedUtilisateur.getPrenom().toLowerCase(), updatedUtilisateur.getId());
        if (NomExiste) {
            // Gérer le cas où l'utilisateur existe déjà
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà .");
            return "redirect:/profil";
        }
        boolean TelExiste = utilisateurRepository.existsByTelAndIdNot(updatedUtilisateur.getTel(), updatedUtilisateur.getId());
        if (TelExiste) {
            // Gérer le cas où l'utilisateur existe déjà
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà .");
            return "redirect:/profil";
        }



        // Enregistrer les modifications dans la base de données
        utilisateurService.save(existingUtilisateur);

        return "redirect:/profil";
    }

    @PostMapping("/profil/updatepass")
    public String updateUserPass(@ModelAttribute("utilisateur") Utilisateur updatedUtilisateur ,  RedirectAttributes redirectAttributes, BindingResult bindingResult, Model model  ) {

        // Récupérer l'utilisateur existant à partir de la base de données
        Utilisateur existingUtilisateur = utilisateurService.getUtilisateurById(updatedUtilisateur.getId());


        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/admin/DetailTeam/" + existingUtilisateur.getId();
        }

        // Mettre à jour les attributs de l'utilisateur existant avec les nouvelles valeurs
        existingUtilisateur.setPassword(passwordEncoder.encode(updatedUtilisateur.getPassword()));



        // Vérifier si l'utilisateur existe déjà
        boolean PassExiste = utilisateurRepository.existsByPassword(updatedUtilisateur.getPassword());
        if (PassExiste) {
            // Gérer le cas où l'utilisateur existe déjà
            redirectAttributes.addFlashAttribute("message", "Changer le mot de passe.");
            return "redirect:/profil";
        }
        // Enregistrer les modifications dans la base de données
        utilisateurService.save(existingUtilisateur);

        return "redirect:/profil";
    }






}
