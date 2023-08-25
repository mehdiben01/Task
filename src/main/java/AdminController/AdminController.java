package AdminController;

import Model.Tache;
import Model.Utilisateur;
import Repository.TacheRepository;
import Repository.UtilisateurRepository;
import Service.ImageService;
import Service.ProjectService;
import Service.TacheService;
import Service.UtilisateurService;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;


@Controller
public class AdminController implements ErrorController {



    private final UtilisateurRepository utilisateurRepository;

    private final UtilisateurService utilisateurService;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final ImageService imageService;

    private final TacheService tacheService;

    private final TacheRepository tacheRepository;
    private final ProjectService projectService;

    public AdminController(UtilisateurRepository utilisateurRepository, UtilisateurService utilisateurService, ImageService imageService, TacheService tacheService, TacheRepository tacheRepository, ProjectService projectService) {
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurService = utilisateurService;
        this.imageService = imageService;
        this.tacheService = tacheService;
        this.tacheRepository = tacheRepository;
        this.projectService = projectService;
    }

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
        public String getIndex(Model model, @RequestParam(required = false) String search,
                               @RequestParam(defaultValue = "0") int page){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Utilisateur utilisateur = utilisateurService.loadUserByUsername(username);
            /* User */
            long countENC = tacheService.countENC(utilisateur.getId());
            long countNonC = tacheService.countNonc(utilisateur.getId());
            long countTermine = tacheService.countTermine(utilisateur.getId());
            long countProject = tacheService.countProject(utilisateur.getId());
            model.addAttribute("tacheEncours",countENC);
            model.addAttribute("tacheNonC",countNonC);
            model.addAttribute("tacheTermine",countTermine);
            model.addAttribute("tacheProject",countProject);
            /* Admin */
            long countT = tacheService.countT();
            long countE = tacheService.countE();
            long countN = tacheService.countN();
            long countR = tacheService.countR();
            model.addAttribute("countT",countT);
            model.addAttribute("countE",countE);
            model.addAttribute("countN",countN);
            model.addAttribute("countR",countR);
            List<Tache> tachesretdebut = tacheService.getTacheRetDebut();
            model.addAttribute("tachesretdebut", tachesretdebut);
            Page<Object[]>  existingProjectsN = projectService.getProjectNcommence("", PageRequest.of(page, 10));
            model.addAttribute("countprojN", existingProjectsN.getTotalElements());
            Page<Object[]>  existingProjectsEnCours = projectService.getProjectEnCours("", PageRequest.of(page, 10));
            model.addAttribute("countprojE", existingProjectsEnCours.getTotalElements());
            Page<Object[]>  existingProjectsTermine = projectService.getProjectTermine("", PageRequest.of(page, 10));
            model.addAttribute("countprojT", existingProjectsTermine.getTotalElements());
            Page<Object[]>  existingProjectsRetard = projectService.getProjectRetarde("", PageRequest.of(page, 10));
            model.addAttribute("countprojR", existingProjectsRetard.getTotalElements());
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
    public String getLogin() {
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
            model.addAttribute("message", "Les données que vous avez fournies sont invalides.");
            return "redirect:/profil";
        }

        // Vérifier si l'utilisateur existe déjà
        boolean DataExiste = utilisateurRepository.existsByNomAndPrenomAndProfessionAndDatenAndTel(updatedUtilisateur.getNom().toUpperCase(),updatedUtilisateur.getPrenom().toLowerCase(),updatedUtilisateur.getProfession().toLowerCase(),updatedUtilisateur.getDaten(),updatedUtilisateur.getTel());
        if (DataExiste) {
            // Gérer le cas où l'utilisateur existe déjà
            redirectAttributes.addFlashAttribute("message", "Les données que vous avez fournies existent déjà.");
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
            redirectAttributes.addFlashAttribute("message", "Les données que vous avez fournies existent déjà.");
            return "redirect:/profil";
        }
        boolean TelExiste = utilisateurRepository.existsByTelAndIdNot(updatedUtilisateur.getTel(), updatedUtilisateur.getId());
        if (TelExiste) {
            // Gérer le cas où l'utilisateur existe déjà
            redirectAttributes.addFlashAttribute("message", "Les données que vous avez fournies existent déjà.");
            return "redirect:/profil";
        }
        if (updatedUtilisateur.getNom().endsWith(" ") || updatedUtilisateur.getNom().startsWith(" ")) {
            redirectAttributes.addFlashAttribute("message", "L'espace indésirable est présent dans le nom.");
            return "redirect:/profil";
        }

        if (updatedUtilisateur.getPrenom().endsWith(" ") || updatedUtilisateur.getPrenom().startsWith(" ")) {
            redirectAttributes.addFlashAttribute("message", "L'espace indésirable est présent dans le prénom.");
            return "redirect:/profil";
        }

        if (updatedUtilisateur.getProfession().endsWith(" ")  || updatedUtilisateur.getProfession().startsWith(" ")) {
            redirectAttributes.addFlashAttribute("message", "L'espace indésirable est présent dans la profession.");
            return "redirect:/profil";
        }

        redirectAttributes.addFlashAttribute("messagesu", "Vos données ont été mises à jour avec succès.");
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

        // Enregistrer les modifications dans la base de données
        redirectAttributes.addFlashAttribute("messagesu", "Vos données ont été mises à jour avec succès.");
        utilisateurService.save(existingUtilisateur);

        return "redirect:/profil";
    }

    @PostMapping("/update/imagep")
    public String updateImageUser(@ModelAttribute("utilisateur") @Valid Utilisateur updatedUtilisateur, @RequestParam("image") MultipartFile imageFile , RedirectAttributes redirectAttributes) {

        // Récupérer l'utilisateur existant à partir de la base de données
        Utilisateur existingUtilisateur = utilisateurService.getUtilisateurById(updatedUtilisateur.getId());

        boolean updateSuccess = imageService.updateImage(existingUtilisateur, imageFile);


            if (imageFile.getSize() > 2 * 1024 * 1024) {
                redirectAttributes.addFlashAttribute("message", ImageService.IMAGE_SIZE_ERROR_MESSAGE);
            } else if (!updateSuccess) {
                redirectAttributes.addFlashAttribute("message", ImageService.IMAGE_SAVE_ERROR_MESSAGE);
            }
            else {
                redirectAttributes.addFlashAttribute("messagepro", "Votre photo de profil a été mise à jour avec succès..");
                utilisateurService.save(existingUtilisateur);
            }
        return "redirect:/profil";
    }




}
