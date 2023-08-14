package Controller;

import Model.Utilisateur;
import Repository.UtilisateurRepository;
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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


@Controller
@RequestMapping("/admin")
public class UtilisateurController  {

    private PasswordEncoder passwordEncoder;

    int elementsPerPage = 6;
    @Autowired
    private final UtilisateurRepository utilisateurRepository;
    @Autowired
    private final UtilisateurService utilisateurService;

    @Autowired
    public UtilisateurController(UtilisateurRepository utilisateurRepository, UtilisateurService utilisateurService, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurService = utilisateurService;
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private UtilisateurService service;

    @Autowired
    private RoleService roleService;

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
        utilisateur.setUsername(utilisateur.getPrenom().toLowerCase().replace(" ","")+""+utilisateur.getNom().toLowerCase().replace(" ","")+""+utilisateur.getDaten().substring((int) 8.10));

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
            long maxSize = 2 * 1024 * 1024; // 2 Mo en octets
            if (imageFile.getSize() > maxSize) {
                // Gérer le cas où la taille de l'image dépasse 2 Mo
                redirectAttributes.addFlashAttribute("message", "La taille de l'image ne peut pas dépasser 2 Mo.");
                return "redirect:/admin/team";
            }
            try {
                String nomImage = imageFile.getOriginalFilename();
                String dossierDestination = "/images/";
                String cheminDestination = System.getProperty("user.dir") + "/src/main/resources/static" + dossierDestination + nomImage;

                // Créer les répertoires si nécessaire
                File dossier = new File(dossierDestination);
                dossier.mkdirs();

                // Créer le fichier de destination
                File fichierDestination = new File(cheminDestination);

                // Enregistrer le contenu de l'image dans le fichier de destination
                FileOutputStream fos = new FileOutputStream(fichierDestination);
                fos.write(imageFile.getBytes());
                fos.close();

                // Définir le chemin de l'image dans l'objet utilisateur
                utilisateur.setCheminImage(dossierDestination + nomImage);
            } catch (IOException e) {
                e.printStackTrace();
                // Gérer l'erreur lors de l'enregistrement du fichier
            }
        }

        // Enregistrer l'utilisateur en utilisant votre service
        redirectAttributes.addFlashAttribute("messagesu", "L'utilisateur a été ajouté avec succès.");
        service.save(utilisateur);

        return "redirect:/admin/team";
    }

    @GetMapping("/DetailTeam/{id}")
    public String getDetailTeam(@PathVariable("id") Integer id, Model model) {
        Utilisateur utilisateur = utilisateurService.getUtilisateurById(id);

        // Add the utilisateur to the model
        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("activePage","detailTeam");
        model.addAttribute("roles", roleService.GetAllRole());
        return "admin/detail-team";
    }

    @PostMapping("/user/update")
    public String updateUser(@ModelAttribute("utilisateur") Utilisateur updatedUtilisateur , RedirectAttributes redirectAttributes, BindingResult bindingResult, Model model  ) {

        // Récupérer l'utilisateur existant à partir de la base de données
        Utilisateur existingUtilisateur = utilisateurService.getUtilisateurById(updatedUtilisateur.getId());
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/admin/DetailTeam/" + existingUtilisateur.getId();
        }

        // Vérifier si l'utilisateur existe déjà
        boolean DataExiste = utilisateurRepository.existsByNomAndPrenomAndProfessionAndDatenAndTel(updatedUtilisateur.getNom().toUpperCase(),updatedUtilisateur.getPrenom().toLowerCase(),updatedUtilisateur.getProfession().toLowerCase(),updatedUtilisateur.getDaten(),updatedUtilisateur.getTel());
        if (DataExiste) {
            // Gérer le cas où l'utilisateur existe déjà
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà.");
            return "redirect:/admin/DetailTeam/" + existingUtilisateur.getId();
        }else{
            // Mettre à jour les attributs de l'utilisateur existant avec les nouvelles valeurs
            existingUtilisateur.setNom(updatedUtilisateur.getNom().toUpperCase());
            existingUtilisateur.setPrenom(updatedUtilisateur.getPrenom().toLowerCase());
            existingUtilisateur.setProfession(updatedUtilisateur.getProfession().toLowerCase());
            existingUtilisateur.setDaten(updatedUtilisateur.getDaten());
            existingUtilisateur.setTel(updatedUtilisateur.getTel());
            existingUtilisateur.setRoles(updatedUtilisateur.getRoles());
            existingUtilisateur.setUsername(updatedUtilisateur.getPrenom().toLowerCase().replace(" ","")+""+updatedUtilisateur.getNom().toLowerCase().replace(" ","")+""+updatedUtilisateur.getDaten().substring(8,10));

        }
        boolean NomExiste = utilisateurRepository.existsByNomAndPrenomAndIdNot(updatedUtilisateur.getNom().toUpperCase(),updatedUtilisateur.getPrenom().toLowerCase(), updatedUtilisateur.getId());
        if (NomExiste) {
            // Gérer le cas où l'utilisateur existe déjà
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà .");
            return "redirect:/admin/DetailTeam/" + existingUtilisateur.getId();
        }
        boolean TelExiste = utilisateurRepository.existsByTelAndIdNot(updatedUtilisateur.getTel(), updatedUtilisateur.getId());
        if (TelExiste) {
            // Gérer le cas où l'utilisateur existe déjà
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà .");
            return "redirect:/admin/DetailTeam/" + existingUtilisateur.getId();
        }



        // Enregistrer les modifications dans la base de données
        utilisateurService.save(existingUtilisateur);

        return "redirect:/admin/DetailTeam/" + existingUtilisateur.getId();
    }


    @PostMapping("/update/imageu")
    public String updateImageUser(@ModelAttribute("utilisateur") @Valid Utilisateur updatedUtilisateur, @RequestParam("image") MultipartFile imageFile , RedirectAttributes redirectAttributes, BindingResult bindingResult, Model model) {

        // Récupérer l'utilisateur existant à partir de la base de données
        Utilisateur existingUtilisateur = utilisateurService.getUtilisateurById(updatedUtilisateur.getId());

        // Vérifier si un nouveau fichier d'image a été sélectionné
        if (!imageFile.isEmpty()) {
            try {
                String nomImage = imageFile.getOriginalFilename();
                String dossierDestination = "/images/";
                String cheminDestination = System.getProperty("user.dir") + "/src/main/resources/static" + dossierDestination + nomImage;

                // Créer les répertoires si nécessaire
                File dossier = new File(dossierDestination);
                dossier.mkdirs();

                // Créer le fichier de destination
                File fichierDestination = new File(cheminDestination);

                // Enregistrer le contenu de l'image dans le fichier de destination
                FileOutputStream fos = new FileOutputStream(fichierDestination);
                fos.write(imageFile.getBytes());
                fos.close();

                // Définir le chemin de l'image dans l'objet utilisateur
                existingUtilisateur.setCheminImage(dossierDestination + nomImage);

                // Enregistrer l'utilisateur mis à jour dans la base de données

            } catch (IOException e) {
                e.printStackTrace();
                // Gérer l'erreur lors de l'enregistrement du fichier
            }
        }
        utilisateurService.save(existingUtilisateur);
        return "redirect:/admin/DetailTeam/" + existingUtilisateur.getId();
    }


    @PostMapping("/user/updatepass")
    public String updateUserPass(@ModelAttribute("utilisateur") Utilisateur updatedUtilisateur , RedirectAttributes redirectAttributes, BindingResult bindingResult, Model model  ) {

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
            return "redirect:/admin/DetailTeam/" + existingUtilisateur.getId();
        }
        // Enregistrer les modifications dans la base de données
        utilisateurService.save(existingUtilisateur);

        return "redirect:/admin/DetailTeam/" + existingUtilisateur.getId();
    }

    @PostMapping("/user/delete")
    public String DeleteUser(@ModelAttribute("utilisateur") Utilisateur updatedUtilisateur , RedirectAttributes redirectAttributes, BindingResult bindingResult, Model model  ) {

        // Récupérer l'utilisateur existant à partir de la base de données
        Utilisateur existingUtilisateur = utilisateurService.getUtilisateurById(updatedUtilisateur.getId());

        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/admin/DetailTeam/" + existingUtilisateur.getId();
        }


        // Mettre à jour les attributs de l'utilisateur existant avec les nouvelles valeurs
        existingUtilisateur.setIsDeleted("1");

        // Enregistrer les modifications dans la base de données
        utilisateurService.save(existingUtilisateur);

        return "redirect:/admin/team";
    }





}
