package AdminController;

import Model.Utilisateur;
import Repository.UtilisateurRepository;
import Service.ImageService;
import Service.RoleService;
import Service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;


@Controller
@RequestMapping("/admin")
public class UtilisateurController  {

    private final PasswordEncoder passwordEncoder;

    int elementsPerPage = 6;
    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurService utilisateurService;

    private final ImageService imageService;

    @Autowired
    public UtilisateurController(UtilisateurRepository utilisateurRepository, UtilisateurService utilisateurService, PasswordEncoder passwordEncoder, ImageService imageService, UtilisateurService service, RoleService roleService) {
        this.passwordEncoder = passwordEncoder;
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurService = utilisateurService;
        this.imageService = imageService;
        this.service = service;
        this.roleService = roleService;
    }
    private final UtilisateurService service;

    private final RoleService roleService;

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
                String cheminImage = utilisateur.getCheminImage();
                if (cheminImage != null) {
                    String privateImageURL = imageService.getPrivateImageURL("taskmanager", cheminImage);
                    model.addAttribute("privateImageURL", privateImageURL);
                }
                // You can add more attributes here
            }
        }
    }





    @GetMapping("/team")
    public String getTeam(Model model, @RequestParam(required = false) String search,
                          @RequestParam(defaultValue = "0") int page) {

        Utilisateur utilisateur = new Utilisateur();
        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("roles", roleService.GetAllRole());
        Page<Object[]> existingUtilisateurs;
        if (search != null && !search.isEmpty()) {
            existingUtilisateurs = utilisateurService.getUtilisateur(search, PageRequest.of(page, elementsPerPage));
        } else {
            existingUtilisateurs = utilisateurService.getUtilisateur("", PageRequest.of(page, elementsPerPage));
        }
        // Récupérez l'URL pré-signée pour chaque client
        for (Object[] clientData : existingUtilisateurs) {
            String cheminImage = (String) clientData[2];
            if (cheminImage != null) {
                String privateImageURL = imageService.getPrivateImageURL("taskmanager", cheminImage);
                clientData[2] = privateImageURL; // Remplacez le chemin de l'image par l'URL pré-signée
            }
        }
        model.addAttribute("utilisateurs", existingUtilisateurs.getContent());
        model.addAttribute("countStaff", existingUtilisateurs.getTotalElements());
        model.addAttribute("activePage", "team");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", existingUtilisateurs.getTotalPages());
        return "admin/equipe";
    }

    @GetMapping("/team-annule")
    public String getTeamSupp(Model model, @RequestParam(required = false) String search,
                          @RequestParam(defaultValue = "0") int page){

        Utilisateur utilisateur = new Utilisateur();
        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("roles", roleService.GetAllRole());
        Page<Object[]> existingUtilisateurSupp;
        if (search != null && !search.isEmpty()) {
            existingUtilisateurSupp = utilisateurService.getUtilisateurSupp(search, PageRequest.of(page, elementsPerPage));
        } else {
            existingUtilisateurSupp = utilisateurService.getUtilisateurSupp("", PageRequest.of(page, elementsPerPage));
        }
        model.addAttribute("utilisateurs", existingUtilisateurSupp.getContent());
        model.addAttribute("countStaff", existingUtilisateurSupp.getTotalElements());
        model.addAttribute("activePage", "team-annule");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", existingUtilisateurSupp.getTotalPages());
        return "admin/equipe-annule";
    }





    @PostMapping("/user/save")
    public String saveUtilisateur(@ModelAttribute @Valid Utilisateur utilisateur, @RequestParam("image") MultipartFile imageFile, HttpServletRequest request, RedirectAttributes redirectAttributes , BindingResult bindingResult, Model model ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/admin/team";
        }
        utilisateur.setNom(utilisateur.getNom().toUpperCase());
        utilisateur.setPrenom(utilisateur.getPrenom().toLowerCase());
        utilisateur.setProfession(utilisateur.getProfession().toLowerCase());
        utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        String dayOfBirth = dateFormat.format(utilisateur.getDaten());
        utilisateur.setUsername(utilisateur.getPrenom().toLowerCase().replace(" ","")+""+utilisateur.getNom().toLowerCase().replace(" ","")+""+dayOfBirth);

        // Vérifier si l'utilisateur existe déjà
        boolean champExiste = utilisateurRepository.existsByUsernameOrTel(utilisateur.getUsername(),  utilisateur.getTel());
        if (champExiste) {
            // Gérer le cas où l'utilisateur existe déjà
            redirectAttributes.addFlashAttribute("message", "L'utilisateur existe déjà.");
            return "redirect:/admin/team";
        }
        boolean NomExiste = utilisateurRepository.existsByNomAndPrenom( utilisateur.getNom(), utilisateur.getPrenom());
        if (NomExiste) {
            // Gérer le cas où l'utilisateur existe déjà
            redirectAttributes.addFlashAttribute("message", "L'utilisateur existe déjà.");
            return "redirect:/admin/team";
        }
        if (!imageFile.isEmpty()) {
            boolean imageUpdateResult = imageService.addImage(utilisateur, imageFile);
            if (!imageUpdateResult) {
                redirectAttributes.addFlashAttribute("message", ImageService.IMAGE_SIZE_ERROR_MESSAGE);
                return "redirect:/admin/team";
            }
        }


        // Enregistrer l'utilisateur en utilisant votre service
        redirectAttributes.addFlashAttribute("messagesu", "L'utilisateur a été ajouté avec succès.");
        service.save(utilisateur);

        return "redirect:/admin/team";
    }

    @GetMapping("/DetailTeam/{username}")
    public String getDetailTeam(@PathVariable("username") String username, Model model) {
        Utilisateur utilisateur = utilisateurService.loadUserByUsername(username);

        if (utilisateur == null ) {
            // Si l'utilisateur n'existe pas ou si l'ID ne correspond pas, affichez une page de refus d'accès
            return "/denied"; // Assurez-vous d'avoir une vue pour la page "denied"
        }
        String cheminImage = utilisateur.getCheminImage();
        if (cheminImage != null) {
            String privateImageURL = imageService.getPrivateImageURL("taskmanager", cheminImage);
            model.addAttribute("image", privateImageURL);
        }
        // Add the utilisateur to the model
        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("activePage","detailTeam");
        model.addAttribute("roles", roleService.GetAllRole());
        return "admin/detail-team";
    }

    @PostMapping("/user/update")
    public String updateUser(@ModelAttribute("utilisateur") Utilisateur updatedUtilisateur , RedirectAttributes redirectAttributes, BindingResult bindingResult, Model model  ) {

        // Récupérer l'utilisateur existant à partir de la base de données
        Utilisateur existingUtilisateur = utilisateurService.loadUserByUsername(updatedUtilisateur.getUsername());
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/admin/DetailTeam/" + existingUtilisateur.getUsername();
        }
        if (updatedUtilisateur.getNom().endsWith(" ") || updatedUtilisateur.getNom().startsWith(" ")) {
            redirectAttributes.addFlashAttribute("message", "L'espace indésirable est présent dans le nom.");
            return "redirect:/admin/DetailTeam/" + existingUtilisateur.getUsername();
        }

        if (updatedUtilisateur.getPrenom().endsWith(" ") || updatedUtilisateur.getPrenom().startsWith(" ")) {
            redirectAttributes.addFlashAttribute("message", "L'espace indésirable est présent dans le prénom.");
            return "redirect:/admin/DetailTeam/" + existingUtilisateur.getUsername();
        }

        if (updatedUtilisateur.getProfession().endsWith(" ") || updatedUtilisateur.getProfession().startsWith(" ")) {
            redirectAttributes.addFlashAttribute("message", "L'espace indésirable est présent dans la profession.");
            return "redirect:/admin/DetailTeam/" + existingUtilisateur.getUsername();
        }
        boolean NomExiste = utilisateurService.existsByNomAndPrenomAndIdNot(updatedUtilisateur.getNom().toUpperCase(),updatedUtilisateur.getPrenom().toLowerCase(), updatedUtilisateur.getId());
        boolean TelExiste = utilisateurService.existsByTelAndIdNot(updatedUtilisateur.getTel(), updatedUtilisateur.getId());
        boolean DataExiste = utilisateurService.existsByNomAndPrenomAndProfessionAndDatenAndTel(updatedUtilisateur.getNom().toUpperCase(),updatedUtilisateur.getPrenom().toLowerCase(),updatedUtilisateur.getProfession().toLowerCase(), updatedUtilisateur.getDaten(),updatedUtilisateur.getTel());
        if (NomExiste) {
            // Gérer le cas où l'utilisateur existe déjà
            redirectAttributes.addFlashAttribute("message", "Les données existent déjà.");
            return "redirect:/admin/DetailTeam/" + existingUtilisateur.getUsername();
        }
        if (TelExiste) {
            // Gérer le cas où l'utilisateur existe déjà
            redirectAttributes.addFlashAttribute("message", "Les données existent déjà.");
            return "redirect:/admin/DetailTeam/" + existingUtilisateur.getUsername();
        }
        if (DataExiste) {
            // Gérer le cas où l'utilisateur existe déjà
            redirectAttributes.addFlashAttribute("message", "Les données existent déjà.");
            return "redirect:/admin/DetailTeam/" + existingUtilisateur.getUsername();
        }

            // Mettre à jour les attributs de l'utilisateur existant avec les nouvelles valeurs
            existingUtilisateur.setNom(updatedUtilisateur.getNom().toUpperCase());
            existingUtilisateur.setPrenom(updatedUtilisateur.getPrenom().toLowerCase());
            existingUtilisateur.setProfession(updatedUtilisateur.getProfession().toLowerCase());
            existingUtilisateur.setTel(updatedUtilisateur.getTel());
            existingUtilisateur.setRoles(updatedUtilisateur.getRoles());
            existingUtilisateur.setDaten(updatedUtilisateur.getDaten());





        redirectAttributes.addFlashAttribute("messagesu", "Les données ont été mises à jour avec succès.");
        // Enregistrer les modifications dans la base de données
        utilisateurService.save(existingUtilisateur);

        return "redirect:/admin/DetailTeam/" + existingUtilisateur.getUsername();
    }


    @PostMapping("/update/imageu")
    public String updateImageUser(@ModelAttribute("utilisateur") @Valid Utilisateur updatedUtilisateur, @RequestParam("image") MultipartFile imageFile , RedirectAttributes redirectAttributes, BindingResult bindingResult, Model model) {

        // Récupérer l'utilisateur existant à partir de la base de données
        Utilisateur existingUtilisateur = utilisateurService.loadUserByUsername(updatedUtilisateur.getUsername());

        // Vérifier si un nouveau fichier d'image a été sélectionné
        boolean updateSuccess = imageService.addImage(existingUtilisateur, imageFile);


        if (imageFile.getSize() > 2 * 1024 * 1024) {
            redirectAttributes.addFlashAttribute("message", ImageService.IMAGE_SIZE_ERROR_MESSAGE);
        } else if (!updateSuccess) {
            redirectAttributes.addFlashAttribute("message", ImageService.IMAGE_SAVE_ERROR_MESSAGE);
        }
        else {
            redirectAttributes.addFlashAttribute("messagepro", "Votre photo de profil a été mise à jour avec succès.");
            utilisateurService.save(existingUtilisateur);
        }

        return "redirect:/admin/DetailTeam/" + existingUtilisateur.getUsername();
    }


    @PostMapping("/user/updatepass")
    public String updateUserPass(@ModelAttribute("utilisateur") Utilisateur updatedUtilisateur , RedirectAttributes redirectAttributes, BindingResult bindingResult, Model model  ) {

        // Récupérer l'utilisateur existant à partir de la base de données
        Utilisateur existingUtilisateur = utilisateurService.getUtilisateurById(updatedUtilisateur.getId());

        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/admin/DetailTeam/" + existingUtilisateur.getId();
        }
        else {
            redirectAttributes.addFlashAttribute("messagepro", "Le mot de passe à été mis à jour avec succès.");
            // Enregistrer les modifications dans la base de données
            utilisateurService.save(existingUtilisateur);
        }


        return "redirect:/admin/DetailTeam/" + existingUtilisateur.getId();
    }

    @PostMapping("/user/delete")
    public String DeleteUser(@ModelAttribute("utilisateur") Utilisateur updatedUtilisateur , RedirectAttributes redirectAttributes, BindingResult bindingResult, Model model  ) {

        // Récupérer l'utilisateur existant à partir de la base de données
        Utilisateur existingUtilisateur = utilisateurService.getUtilisateurById(updatedUtilisateur.getId());

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/admin/DetailTeam/" + existingUtilisateur.getId();
        }else {
            // Mettre à jour les attributs de l'utilisateur existant avec les nouvelles valeurs
            existingUtilisateur.setIsDeleted("1");
            // Enregistrer les modifications dans la base de données
            redirectAttributes.addFlashAttribute("messagesu", "Le compte de a été fermé avec succès.");
            utilisateurService.save(existingUtilisateur);
        }
        return "redirect:/admin/team";
    }
    @PostMapping("/user/recup")
    public String RecupUser(@ModelAttribute("utilisateur") Utilisateur updatedUtilisateur , RedirectAttributes redirectAttributes, BindingResult bindingResult, Model model  ) {

        // Récupérer l'utilisateur existant à partir de la base de données
        Utilisateur existingUtilisateur = utilisateurService.getUtilisateurById(updatedUtilisateur.getId());

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/admin/DetailTeam/" + existingUtilisateur.getId();
        }else {
            // Mettre à jour les attributs de l'utilisateur existant avec les nouvelles valeurs
            existingUtilisateur.setIsDeleted("0");
            // Enregistrer les modifications dans la base de données
            redirectAttributes.addFlashAttribute("messagesu", "Le compte de a été recupéré avec succès.");
            utilisateurService.save(existingUtilisateur);
        }
            return "redirect:/admin/team-annule";
    }





}
