package Controller;

import Model.Client;
import Model.Project;
import Model.Utilisateur;
import Repository.ProjectRepository;
import Service.ClientService;
import Service.ProjectService;
import Service.TacheService;
import Service.UtilisateurService;
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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
public class ProjectController {

    int elementsPerPage = 6;

    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TacheService tacheService;

    @Autowired
    private ClientService clientService;

    @Autowired
    public ProjectController(ProjectService projectService, ProjectRepository projectRepository){
        this.projectRepository = projectRepository;
        this.projectService = projectService;
    }

    @Autowired
    private ServletContext servletContext;
    @Autowired
    private ClientService service;

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
        model.addAttribute("search", search); // Add the search parameter to the model
        model.addAttribute("countTer",tacheService.countPTermine());
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
        model.addAttribute("countEnC",tacheService.countENC());
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
        model.addAttribute("countNonC",tacheService.countNonc());
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



        // Enregistrer l'utilisateur en utilisant votre service
        redirectAttributes.addFlashAttribute("messagesu", "Le projet a été ajouté avec succès.");
        projectService.save(project);

        return "redirect:/projet";
    }

    @GetMapping("DetailProjet/{id}")
    public String getDetailProject(@PathVariable("id") Integer id, Model model){
        List<Object[]> project = projectService.getProjectDetailsById(id);
        // Transmettre les données récupérées à la vue
        model.addAttribute("project", project);
        Project projet = (Project) project.get(0)[0];

        // Récupérer la date de fin du projet
        LocalDate dateFinProjet = LocalDate.parse(projet.getDatef());

        // Calculer la différence entre la date de fin du projet et la date actuelle
        long joursRestants = ChronoUnit.DAYS.between(LocalDate.now(), dateFinProjet);

        // Transmettre le nombre de jours restants au modèle
        model.addAttribute("joursRestants", joursRestants);
        Project projects = projectService.getProjectById(id);
        model.addAttribute("projects", projects);
        model.addAttribute("activePage","DetailProjet");
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
            return "redirect:/EditProject/" + existingProject.getId();
        }
        boolean TDCNExiste = projectRepository.existsByTitleAndDescriptionAndClientsNot(updateProject.getTitle().toUpperCase(), updateProject.getDescription().toLowerCase(), updateProject.getClients());
        if(TDCNExiste){
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà.");
            return "redirect:/EditProject/" + existingProject.getId();
        }
        boolean TCExiste = projectRepository.existsByTitleAndClients(updateProject.getTitle().toUpperCase(), updateProject.getClients());
        if(TCExiste){
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà.");
            return "redirect:/EditProject/" + existingProject.getId();
        }
        boolean TCNExiste = projectRepository.existsByTitleAndClients(updateProject.getTitle().toUpperCase(), updateProject.getClients());
        if(TCNExiste){
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà.");
            return "redirect:/EditProject/" + existingProject.getId();
        }
        boolean DejaExiste = projectRepository.existsByTitleAndDescriptionAndDatedAndDatefAndClients(updateProject.getTitle().toUpperCase(), updateProject.getDescription().toLowerCase(), updateProject.getDated(),updateProject.getDatef(), updateProject.getClients());
        if (DejaExiste) {
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà.");
            return "redirect:/EditProject/" + existingProject.getId();
        }else{
            existingProject.setTitle(updateProject.getTitle().toUpperCase());
            existingProject.setDescription(updateProject.getDescription().toLowerCase());
            existingProject.setDated(updateProject.getDated());
            existingProject.setDatef(updateProject.getDatef());
            existingProject.setClients(updateProject.getClients());
        }
        projectService.save(existingProject);
        return "redirect:/EditProject/" + existingProject.getId();

    }

    @PostMapping("/project/delete")
    public String projectDelete(@ModelAttribute("project") Project deleteP, RedirectAttributes redirectAttributes, Model model, BindingResult bindingResult ){
        Project existingProject = projectService.getProjectById(deleteP.getId());
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/projet" ;
        }
        existingProject.setIsDeleted("1");
        projectService.save(existingProject);
        return "redirect:/projet";

    }


}
