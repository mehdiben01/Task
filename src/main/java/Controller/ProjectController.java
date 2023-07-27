package Controller;

import Model.Client;
import Model.Project;
import Repository.ProjectRepository;
import Service.ClientService;
import Service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
public class ProjectController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectRepository projectRepository;

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

    @GetMapping("/projet")
    public String getProjet(Model model, @RequestParam(defaultValue = "0") int page) {
        int pageSize = 3; // Number of projects to display per page
        PageRequest pageable = PageRequest.of(page, pageSize);

        // Retrieve existing projects with average etat using pagination
        Page<Object[]> existingProjectsAndAverageEtat = projectService.getExistingProjectsAndAverageEtat(pageable);
        model.addAttribute("countproj",projectService.countProjects());
        model.addAttribute("projects", existingProjectsAndAverageEtat.getContent());
        model.addAttribute("activePage", "projet");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", existingProjectsAndAverageEtat.getTotalPages());

        // Add an empty Project object to the model
        model.addAttribute("project", new Project());

        return "admin/project";
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

    @GetMapping("DetailProject/{id}")
    public String getDetailProject(@PathVariable("id") Integer id, Model model){
        Project project = projectService.getProjectById(id);
        model.addAttribute("project", project);
        List<Client> clients = clientService.getAllClient();
        model.addAttribute("clients", clients);
        model.addAttribute("activePage", "detailProject");
        return "admin/detail-project";
    }

    @PostMapping("/project/update")
    public String updateProject(@ModelAttribute("project") Project updateProject , BindingResult bindingResult, RedirectAttributes redirectAttributes , Model model){
        Project existingProject = projectService.getProjectById(updateProject.getId());
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà.");
            return "redirect:/DetailProject/" + existingProject.getId();
        }
        boolean TDCNExiste = projectRepository.existsByTitleAndDescriptionAndClientsNot(updateProject.getTitle().toUpperCase(), updateProject.getDescription().toLowerCase(), updateProject.getClients());
        if(TDCNExiste){
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà.");
            return "redirect:/DetailProject/" + existingProject.getId();
        }
        boolean TCExiste = projectRepository.existsByTitleAndClients(updateProject.getTitle().toUpperCase(), updateProject.getClients());
        if(TCExiste){
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà.");
            return "redirect:/DetailProject/" + existingProject.getId();
        }
        boolean TCNExiste = projectRepository.existsByTitleAndClients(updateProject.getTitle().toUpperCase(), updateProject.getClients());
        if(TCNExiste){
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà.");
            return "redirect:/DetailProject/" + existingProject.getId();
        }
        boolean DejaExiste = projectRepository.existsByTitleAndDescriptionAndDatedAndDatefAndClients(updateProject.getTitle().toUpperCase(), updateProject.getDescription().toLowerCase(), updateProject.getDated(),updateProject.getDatef(), updateProject.getClients());
        if (DejaExiste) {
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà.");
            return "redirect:/DetailProject/" + existingProject.getId();
        }else{
            existingProject.setTitle(updateProject.getTitle().toUpperCase());
            existingProject.setDescription(updateProject.getDescription().toLowerCase());
            existingProject.setDated(updateProject.getDated());
            existingProject.setDatef(updateProject.getDatef());
            existingProject.setClients(updateProject.getClients());
        }
        projectService.save(existingProject);
        return "redirect:/DetailProject/" + existingProject.getId();

    }

    @PostMapping("/project/delete")
    public String projectDelete(@ModelAttribute("project") Project deleteP, RedirectAttributes redirectAttributes, Model model, BindingResult bindingResult ){
        Project existingProject = projectService.getProjectById(deleteP.getId());
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/tache" ;
        }
        existingProject.setIsDeleted("1");
        projectService.save(existingProject);
        return "redirect:/projet";

    }


}
