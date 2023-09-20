package AdminController;

import Model.Client;
import Model.Project;
import Model.Tache;
import Model.Utilisateur;
import Repository.ProjectRepository;
import Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class ProjectController {

    int elementsPerPage = 6;

    private final ProjectService projectService;
    private final ProjectRepository projectRepository;

    private final TacheService tacheService;

    private final ClientService clientService;

    private final ImageService imageService;

    @Autowired
    public ProjectController(ProjectService projectService, ProjectRepository projectRepository, TacheService tacheService, ClientService clientService, ImageService imageService, ClientService service, UtilisateurService utilisateurService){
        this.projectService = projectService;
        this.projectRepository = projectRepository;
        this.tacheService = tacheService;
        this.clientService = clientService;
        this.imageService = imageService;
        this.service = service;
        this.utilisateurService = utilisateurService;
    }


    private final ClientService service;

    private final UtilisateurService utilisateurService;

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


    @GetMapping("/projet")
    public String getProjet(Model model, @RequestParam(required = false) String search,
                            @RequestParam(defaultValue = "0") int page) {


        Project project = new Project();
        model.addAttribute("project", project);
        model.addAttribute("clients", service.getAllClient());


        Page<Object[]> existingProjectsAndAverageEtatPage;

        if (search != null && !search.isEmpty()) {
            existingProjectsAndAverageEtatPage = projectService.getExistingProjectsAndAverageEtatByTitle(search, PageRequest.of(page, elementsPerPage));
        } else {
            existingProjectsAndAverageEtatPage = projectService.getExistingProjectsAndAverageEtatByTitle("", PageRequest.of(page, elementsPerPage));
        }
        // Récupérez l'URL pré-signée pour chaque membre
        for (Object[] projectData : existingProjectsAndAverageEtatPage) {
            Project projectItem = (Project) projectData[0];
            List<Tache> taches = projectItem.getTaches();
            for (Tache tache : taches) {
                if (tache.getUsers() != null) {
                    String cheminImage = tache.getUsers().getCheminImage();
                    if (cheminImage != null) {
                        String privateImageURL = imageService.getPrivateImageURL("taskmanager", cheminImage);
                        tache.getUsers().setCheminImage(privateImageURL);
                    }
                }
            }
        }

        // Récupérez l'URL pré-signée pour chaque client
        for (Object[] clientData : existingProjectsAndAverageEtatPage) {
            String cheminImage = (String) clientData[4];
            if (cheminImage != null) {
                String privateImageURL = imageService.getPrivateImageURL("taskmanager", cheminImage);
                clientData[4] = privateImageURL; // Remplacez le chemin de l'image par l'URL pré-signée
            }
        }


        model.addAttribute("projects", existingProjectsAndAverageEtatPage.getContent());
        model.addAttribute("countproj", existingProjectsAndAverageEtatPage.getTotalElements());
        model.addAttribute("activePage", "projet");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", existingProjectsAndAverageEtatPage.getTotalPages());
        model.addAttribute("search", search); // Add the search parameter to the model

        return "admin/project";
    }

    @GetMapping("/projet-termine")
    public String getProjetTermine(Model model, @RequestParam(required = false) String search,
                            @RequestParam(defaultValue = "0") int page) {
        Project project = new Project();
        model.addAttribute("project", project);
        model.addAttribute("clients", service.getAllClient());

        Page<Object[]> existingProjectsTermine;
        if (search != null && !search.isEmpty()) {
            existingProjectsTermine = projectService.getProjectTermine(search, PageRequest.of(page, elementsPerPage));
        } else {
            existingProjectsTermine = projectService.getProjectTermine("", PageRequest.of(page, elementsPerPage));
        }
        model.addAttribute("projects", existingProjectsTermine.getContent());
        model.addAttribute("activePage", "projet-termine");
        model.addAttribute("countproj", existingProjectsTermine.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", existingProjectsTermine.getTotalPages());
        model.addAttribute("search", search);
        return "admin/project-termine";
    }

    @GetMapping("/projet-encours")
    public String getProjetEnCours(Model model, @RequestParam(required = false) String search,
                                   @RequestParam(defaultValue = "0") int page) {
        Project project = new Project();
        model.addAttribute("project", project);
        model.addAttribute("clients", service.getAllClient());
              Page<Object[]> existingProjectsEnCours;
        if (search != null && !search.isEmpty()) {
            existingProjectsEnCours = projectService.getProjectEnCours(search, PageRequest.of(page, elementsPerPage));
        } else {
            existingProjectsEnCours = projectService.getProjectEnCours("", PageRequest.of(page, elementsPerPage));
        }
        model.addAttribute("projects", existingProjectsEnCours.getContent());
        model.addAttribute("countproj", existingProjectsEnCours.getTotalElements());
        model.addAttribute("activePage", "projet-encours");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", existingProjectsEnCours.getTotalPages());
        model.addAttribute("search", search); // Add the search parameter to the model
        return "admin/project-encours";
    }
    @GetMapping("/projet-noncommence")
    public String getProjetNcommence(Model model, @RequestParam(required = false) String search,
                                   @RequestParam(defaultValue = "0") int page) {
        Project project = new Project();
        model.addAttribute("project", project);
        model.addAttribute("clients", service.getAllClient());
        Page<Object[]> existingProjectsNcommence;
        if (search != null && !search.isEmpty()) {
            existingProjectsNcommence = projectService.getProjectNcommence(search, PageRequest.of(page, elementsPerPage));
        } else {
            existingProjectsNcommence = projectService.getProjectNcommence("", PageRequest.of(page, elementsPerPage));
        }
        model.addAttribute("projects", existingProjectsNcommence.getContent());
        model.addAttribute("countproj", existingProjectsNcommence.getTotalElements());
        model.addAttribute("activePage", "projet-noncommence");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", existingProjectsNcommence.getTotalPages());
        model.addAttribute("search", search); // Add the search parameter to the model
        return "admin/project-noncommence";
    }

    @GetMapping("/projet-annule")
    public String getProjetAnnule(Model model, @RequestParam(required = false) String search,
                                  @RequestParam(defaultValue = "0") int page) {
        Project project = new Project();
        model.addAttribute("project", project);
        model.addAttribute("clients", service.getAllClient());
        Page<Object[]> existingProjectsAnnule;
        if (search != null && !search.isEmpty()) {
            existingProjectsAnnule = projectService.getProjectAnnule(search, PageRequest.of(page, elementsPerPage));
        } else {
            existingProjectsAnnule = projectService.getProjectAnnule("", PageRequest.of(page, elementsPerPage));
        }
        model.addAttribute("projects", existingProjectsAnnule.getContent());
        model.addAttribute("countproj", existingProjectsAnnule.getTotalElements());
        model.addAttribute("activePage", "projet-annule");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", existingProjectsAnnule.getTotalPages());
        model.addAttribute("search", search); // Add the search parameter to the model
        model.addAttribute("countAnnule",projectService.countProjectsDeleted());
        return "admin/project-annule";
    }

    @GetMapping("/projet-retarde")
    public String getProjetRetarde(Model model, @RequestParam(required = false) String search,
                                  @RequestParam(defaultValue = "0") int page) {
        Project project = new Project();
        model.addAttribute("project", project);
        model.addAttribute("clients", service.getAllClient());
        Page<Object[]> existingProjectsRetarde;
        if (search != null && !search.isEmpty()) {
            existingProjectsRetarde = projectService.getProjectRetarde(search, PageRequest.of(page, elementsPerPage));
        } else {
            existingProjectsRetarde = projectService.getProjectRetarde("", PageRequest.of(page, elementsPerPage));
        }
        model.addAttribute("projects", existingProjectsRetarde.getContent());
        model.addAttribute("activePage", "projet-retarde");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", existingProjectsRetarde.getTotalPages());
        model.addAttribute("search", search); // Add the search parameter to the model
        model.addAttribute("countproj", existingProjectsRetarde.getTotalElements());
        return "admin/project-retarde";
    }


    @PostMapping("/project/save")
    public String saveUtilisateur(@ModelAttribute @Valid Project project, HttpServletRequest request, RedirectAttributes redirectAttributes , BindingResult bindingResult, Model model ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/projet";
        }
        // Vérifier si l'utilisateur existe déjà
        boolean champExiste = projectRepository.existsByTitleOrDescription(project.getTitle(), project.getDescription());
        if (champExiste) {
            // Gérer le cas où l'utilisateur existe déjà
            redirectAttributes.addFlashAttribute("message", "Le projet existe déjà.");
            return "redirect:/projet";
        }

        project.setTitle(project.getTitle().toUpperCase());
        project.setDescription(project.getDescription().toLowerCase());

        // Enregistrer l'utilisateur en utilisant votre service
        redirectAttributes.addFlashAttribute("messagesu", "Le projet a été ajouté avec succès.");
        projectService.save(project);

        return "redirect:/admin/projet";
    }

    @GetMapping("DetailProjet/{id}")
    public String getDetailProject(@PathVariable("id") Integer id, Model model){
        List<Object[]> project = projectService.getProjectDetailsById(id);
        for (Object[] projectData : project) {
            Project projectItem = (Project) projectData[0];
            List<Tache> taches = projectItem.getTaches();
            for (Tache tache : taches) {
                if (tache.getUsers() != null) {
                    String cheminImage = tache.getUsers().getCheminImage();
                    if (cheminImage != null) {
                        String privateImageURL = imageService.getPrivateImageURL("taskmanager", cheminImage);
                        tache.getUsers().setCheminImage(privateImageURL);
                    }
                }
            }
        }
        // Transmettre les données récupérées à la vue
        model.addAttribute("project", project);
        Project projet = (Project) project.get(0)[0];

        // Récupérer la date de fin du projet
        Date dateFinProjet = projet.getDatef();
        // Obtenir la date d'aujourd'hui
        Date dateAujourdhui = new Date();

        // Calculer la différence en millisecondes entre les deux dates
        long differenceEnMillis = dateFinProjet.getTime() - dateAujourdhui.getTime();

        // Convertir la différence en jours
        long joursRestants = differenceEnMillis / (24 * 60 * 60 * 1000);
        // Transmettre le nombre de jours restants au modèle
        model.addAttribute("joursRestants", joursRestants);
        Project projects = projectService.getProjectById(id);
        model.addAttribute("projects", projects);
        model.addAttribute("activePage","DetailProjet");
        String cheminImageProject = projects.getClients().getCheminImage();
        if (cheminImageProject != null) {
            String privateImageURL = imageService.getPrivateImageURL("taskmanager", cheminImageProject);
            model.addAttribute("CompanyImage", privateImageURL);
        }
        return "admin/detail-projet";
    }

    @GetMapping("EditProject/{id}")
    public String getEditProject(@PathVariable("id") Integer id, Model model){
        Project project = projectService.getProjectById(id);
        model.addAttribute("project", project);
        List<Client> clients = clientService.getAllClient();
        model.addAttribute("clients", clients);
        model.addAttribute("activePage", "EditProjet");
        return "admin/edit-project";
    }

    @PostMapping("/project/update")
    public String updateProject(@ModelAttribute("project") Project updateProject , BindingResult bindingResult, RedirectAttributes redirectAttributes , Model model){
        Project existingProject = projectService.getProjectById(updateProject.getId());
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà.");
            return "redirect:/admin/EditProject/" + existingProject.getId();
        }
        boolean TDCNExiste = projectRepository.existsByTitleAndDescriptionAndClientsNot(updateProject.getTitle().toUpperCase(), updateProject.getDescription().toLowerCase(), updateProject.getClients());
        if(TDCNExiste){
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà.");
            return "redirect:/admin/EditProject/" + existingProject.getId();
        }
        boolean TCNExiste = projectRepository.existsByTitleAndIdNot(updateProject.getTitle().toUpperCase(), updateProject.getId());
        if(TCNExiste){
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà.");
            return "redirect:/admin/EditProject/" + existingProject.getId();
        }
        boolean DejaExiste = projectRepository.existsByTitleAndDescriptionAndDatedAndDatefAndClients(updateProject.getTitle().toUpperCase(), updateProject.getDescription().toLowerCase(), updateProject.getDated(),updateProject.getDatef(), updateProject.getClients());
        if (DejaExiste) {
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà.");
            return "redirect:/admin/EditProject/" + existingProject.getId();
        }else{
            existingProject.setTitle(updateProject.getTitle().toUpperCase());
            existingProject.setDescription(updateProject.getDescription().toLowerCase());
            existingProject.setDated(updateProject.getDated());
            existingProject.setDatef(updateProject.getDatef());
            existingProject.setClients(updateProject.getClients());
        }
        redirectAttributes.addFlashAttribute("messagesu", "Le projet a été mis à jour avec succès.");
        projectService.save(existingProject);
        return "redirect:/admin/EditProject/" + existingProject.getId();

    }

    @PostMapping("/project/delete")
    public String projectDelete(@ModelAttribute("project") Project projetc, RedirectAttributes redirectAttributes, Model model, BindingResult bindingResult ){
        Project existingProject = projectService.getProjectById(projetc.getId());
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/projet" ;
        }
        redirectAttributes.addFlashAttribute("messagesu", "Le projet a été annuler avec succès.");
        existingProject.setIsDeleted("1");
        projectService.save(existingProject);
        return "redirect:/admin/projet";

    }

    @PostMapping("/project/recup")
    public String projectRecup(@ModelAttribute("project") Project projetc, RedirectAttributes redirectAttributes, Model model, BindingResult bindingResult ){
        Project existingProject = projectService.getProjectById(projetc.getId());
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/admin/projet" ;
        }
        redirectAttributes.addFlashAttribute("messagesu", "Le projet a été récuperer avec succès.");
        existingProject.setIsDeleted("0");
        projectService.save(existingProject);
        return "redirect:/admin/projet";

    }


}
