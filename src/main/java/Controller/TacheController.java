package Controller;

import Model.Project;
import Model.Tache;
import Model.Utilisateur;
import Repository.ProjectRepository;
import Repository.TacheRepository;
import Service.ProjectService;
import Service.TacheService;
import Service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TacheController {

    List<String> errors = new ArrayList<>();
    @Autowired
    private TacheRepository tacheRepository;
    @Autowired

    private TacheService tacheService;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    public  TacheController(TacheRepository tacheRepository, TacheService tacheService){
        this.tacheRepository = tacheRepository;
        this.tacheService = tacheService;
    }

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private ProjectService projects;


    @GetMapping("/tache")
    public String getTache(Model model){
        Tache tache = new Tache();
        model.addAttribute("tache",tache);
        model.addAttribute("users",utilisateurService.getAllUtilisateurs() );
        model.addAttribute("projects", projects.getAllProject());
        model.addAttribute("task",tacheService.getAllTask());
        model.addAttribute("activePage", "tache");
        return "admin/tache";
    }
    @PostMapping("/tache/save")
    public String saveUtilisateur(@ModelAttribute @Valid Tache tache, HttpServletRequest request, RedirectAttributes redirectAttributes , BindingResult bindingResult, Model model ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/tache";
        }
        // Retrieve the project's start and end dates
        List<Object[]> projectDatesList = projectRepository.findProjectDatesById(tache.getProject().getId());



        Object[] projectDates = projectDatesList.get(0); // Retrieve the first row

        // Check if the task's start date is after the project's start date
        LocalDate taskStartDate = LocalDate.parse(tache.getDated());
        LocalDate projectStartDate = LocalDate.parse(projectDates[0].toString());
        if (taskStartDate.isBefore(projectStartDate)) {
            redirectAttributes.addFlashAttribute("errors", "Vérifier les dates du projet.");
            return "redirect:/tache";
        }

        // Check if the task's end date is before the project's end date
        LocalDate taskEndDate = LocalDate.parse(tache.getDatef());
        LocalDate projectEndDate = LocalDate.parse(projectDates[1].toString());
        if (taskEndDate.isAfter(projectEndDate)) {
            redirectAttributes.addFlashAttribute("errors", "Vérifier les dates du projet.");
            return "redirect:/tache";
        }

        // Vérifier si l'utilisateur existe déjà
        boolean champExiste = tacheRepository.existsByTitleAndProjectAndUsers(tache.getTitle(), tache.getProject(), tache.getUsers());
        if (champExiste) {
            // Gérer le cas où l'utilisateur existe déjà
            redirectAttributes.addFlashAttribute("message", "La tache existe déjà.");
            return "redirect:/tache";
        }



        // Enregistrer l'utilisateur en utilisant votre service
        redirectAttributes.addFlashAttribute("messagesu", "La tache a été ajouté avec succès.");
        tacheService.save(tache);

        return "redirect:/tache";
    }

    @GetMapping("DetailTache/{id}")
    public String getDetailTache(@PathVariable("id") Integer id, Model model){
        Tache tache = tacheService.getTacheById(id);
        model.addAttribute("tache", tache);
        List<Utilisateur> utilisateur = utilisateurService.getAllUtilisateurs();
        model.addAttribute("utilisateur", utilisateur);
        List<Project> project = projects.getAllProject();
        model.addAttribute("project",project);
        model.addAttribute("activePage","detailTache");
       return "admin/detail-tache";
    }

    @PostMapping("/tache/update")
    public String updateTache(@ModelAttribute("tache") Tache updateTache, RedirectAttributes redirectAttributes, Model model, BindingResult  bindingResult){
       Tache existingTache = tacheService.getTacheById(updateTache.getId());
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/DetailTache/" + existingTache.getId();
        }

        boolean DejaExiste = tacheRepository.existsByTitleAndDescriptionAndDatedAndDatefAndUsersAndProjectAndEtat(updateTache.getTitle(), updateTache.getDescription(), updateTache.getDated(), updateTache.getDatef(), updateTache.getUsers(), updateTache.getProject(), updateTache.getEtat());
        if (DejaExiste) {
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà.");
            return "redirect:/DetailTache/" + existingTache.getId();
        }
        boolean NPexiste = tacheRepository.existsByTitleAndProjectAndUsersAndIdNot(updateTache.getTitle(), updateTache.getProject(), updateTache.getUsers(), updateTache.getId());
        if (NPexiste) {
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà.");
            return "redirect:/DetailTache/" + existingTache.getId();
        }else {
            existingTache.setTitle(updateTache.getTitle());
            existingTache.setDescription(updateTache.getDescription());
            existingTache.setDated(updateTache.getDated());
            existingTache.setDatef(updateTache.getDatef());
            existingTache.setEtat(updateTache.getEtat());
            existingTache.setProject(updateTache.getProject());
            existingTache.setUsers(updateTache.getUsers());
        }

        tacheService.save(existingTache);
        return  "redirect:/DetailTache/" + existingTache.getId();

    }
}
