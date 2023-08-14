package Controller;

import Model.Project;
import Model.Tache;
import Model.Utilisateur;
import Repository.ProjectRepository;
import Repository.TacheRepository;
import Repository.TacheSupprimeeRepository;
import Service.ProjectService;
import Service.TacheService;
import Service.TacheSupprimeeService;
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
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class TacheController {

    int elementsPerPage = 6;

    List<String> errors = new ArrayList<>();
    @Autowired
    private TacheRepository tacheRepository;
    @Autowired

    private TacheService tacheService;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TacheSupprimeeService tacheSupprimeeService;

    @Autowired
    private TacheSupprimeeRepository tacheSupprimeeRepository;

    @Autowired
    public  TacheController(TacheRepository tacheRepository, TacheService tacheService, TacheSupprimeeService tacheSupprimeeService){
        this.tacheRepository = tacheRepository;
        this.tacheService = tacheService;
        this.tacheSupprimeeService = tacheSupprimeeService;
    }

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private ProjectService projects;


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



    @GetMapping("/tache")
    public String getTache(Model model, @RequestParam(required = false) String search,
                           @RequestParam(defaultValue = "0") int page){
        Tache tache = new Tache();
        model.addAttribute("tache",tache);
        model.addAttribute("users",utilisateurService.getAllUtilisateurs() );
        model.addAttribute("projects", projects.getAllProject());
        model.addAttribute("activePage", "tache");
        Page<Object[]> existingTache;
        if (search != null && !search.isEmpty()) {
            existingTache = tacheService.getAllTask(search, PageRequest.of(page, elementsPerPage));
        } else {
            existingTache = tacheService.getAllTask("", PageRequest.of(page, elementsPerPage));
        }
        model.addAttribute("task", existingTache.getContent());
        model.addAttribute("count",existingTache.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", existingTache.getTotalPages());
        model.addAttribute("search", search); // Add the search parameter to the model
        return "admin/tache";
    }
    @GetMapping("/tache-annule")
    public String getTacheAnnuler(Model model, @RequestParam(required = false) String search,
                           @RequestParam(defaultValue = "0") int page){
        Tache tache = new Tache();
        model.addAttribute("tache",tache);
        model.addAttribute("users",utilisateurService.getAllUtilisateurs() );
        model.addAttribute("projects", projects.getAllProject());
        model.addAttribute("activePage", "tache-annule");
        Page<Object[]> existingTacheSupp;
        if (search != null && !search.isEmpty()) {
            existingTacheSupp = tacheSupprimeeService.getAllTacheSupprimeeList(search, PageRequest.of(page, elementsPerPage));
        } else {
            existingTacheSupp = tacheSupprimeeService.getAllTacheSupprimeeList("", PageRequest.of(page, elementsPerPage));
        }
        model.addAttribute("task", existingTacheSupp.getContent());
        model.addAttribute("counttache", existingTacheSupp.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", existingTacheSupp.getTotalPages());
        model.addAttribute("search", search); // Add the search parameter to the model
        return "admin/tache-annule";
    }
    @GetMapping("/tache-encours")
    public String getTacheEncours(Model model, @RequestParam(required = false) String search,
                                  @RequestParam(defaultValue = "0") int page){
        Tache tache = new Tache();
        model.addAttribute("tache",tache);
        model.addAttribute("users",utilisateurService.getAllUtilisateurs() );
        model.addAttribute("projects", projects.getAllProject());
        model.addAttribute("activePage", "tache-encours");
        Page<Object[]> existingTacheEncours;
        if (search != null && !search.isEmpty()) {
            existingTacheEncours = tacheService.getTaskEncours(search, PageRequest.of(page, elementsPerPage));
        } else {
            existingTacheEncours = tacheService.getTaskEncours("", PageRequest.of(page, elementsPerPage));
        }
        model.addAttribute("task", existingTacheEncours.getContent());
        model.addAttribute("count",existingTacheEncours.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", existingTacheEncours.getTotalPages());
        model.addAttribute("search", search); // Add the search parameter to the model
        return "admin/tache-encours";
    }
    @GetMapping("/tache-noncommence")
    public String getTacheNonC(Model model, @RequestParam(required = false) String search,
                                  @RequestParam(defaultValue = "0") int page){
        Tache tache = new Tache();
        model.addAttribute("tache",tache);
        model.addAttribute("users",utilisateurService.getAllUtilisateurs() );
        model.addAttribute("projects", projects.getAllProject());
        model.addAttribute("activePage", "tache-noncommence");
        Page<Object[]> existingTacheNonC;
        if (search != null && !search.isEmpty()) {
            existingTacheNonC = tacheService.getTaskNonC(search, PageRequest.of(page, elementsPerPage));
        } else {
            existingTacheNonC = tacheService.getTaskNonC("", PageRequest.of(page, elementsPerPage));
        }
        model.addAttribute("task", existingTacheNonC.getContent());
        model.addAttribute("count",existingTacheNonC.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", existingTacheNonC.getTotalPages());
        model.addAttribute("search", search); // Add the search parameter to the model
        return "admin/tache-noncommence";
    }
    @GetMapping("/tache-retarde")
    public String getTacheRet(Model model, @RequestParam(required = false) String search,
                               @RequestParam(defaultValue = "0") int page){
        Tache tache = new Tache();
        model.addAttribute("tache",tache);
        model.addAttribute("users",utilisateurService.getAllUtilisateurs() );
        model.addAttribute("projects", projects.getAllProject());
        model.addAttribute("activePage", "tache-retarde");
        Page<Object[]> existingTacheRet;
        if (search != null && !search.isEmpty()) {
            existingTacheRet = tacheService.getTaskRet(search, PageRequest.of(page, elementsPerPage));
        } else {
            existingTacheRet = tacheService.getTaskRet("", PageRequest.of(page, elementsPerPage));
        }
        model.addAttribute("task", existingTacheRet.getContent());
        model.addAttribute("count",existingTacheRet.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", existingTacheRet.getTotalPages());
        model.addAttribute("search", search); // Add the search parameter to the model
        return "admin/tache-retarde";
    }
    @GetMapping("/tache-termine")
    public String getTacheTermine(Model model, @RequestParam(required = false) String search,
                              @RequestParam(defaultValue = "0") int page){
        Tache tache = new Tache();
        model.addAttribute("tache",tache);
        model.addAttribute("users",utilisateurService.getAllUtilisateurs() );
        model.addAttribute("projects", projects.getAllProject());
        model.addAttribute("activePage", "tache-termine");
        Page<Object[]> existingTacheTermine;
        if (search != null && !search.isEmpty()) {
            existingTacheTermine = tacheService.getTaskTermine(search, PageRequest.of(page, elementsPerPage));
        } else {
            existingTacheTermine = tacheService.getTaskTermine("", PageRequest.of(page, elementsPerPage));
        }
        model.addAttribute("task", existingTacheTermine.getContent());
        model.addAttribute("count",existingTacheTermine.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", existingTacheTermine.getTotalPages());
        model.addAttribute("search", search); // Add the search parameter to the model
        return "admin/tache-termine";
    }
    @GetMapping("/tache-termineretard")
    public String getTacheTermineEnRet(Model model, @RequestParam(required = false) String search,
                                  @RequestParam(defaultValue = "0") int page){
        Tache tache = new Tache();
        model.addAttribute("tache",tache);
        model.addAttribute("users",utilisateurService.getAllUtilisateurs() );
        model.addAttribute("projects", projects.getAllProject());
        model.addAttribute("activePage", "tache-termineretard");
        Page<Object[]> existingTacheTermineEnRet;
        if (search != null && !search.isEmpty()) {
            existingTacheTermineEnRet = tacheService.getTaskTermineEnRet(search, PageRequest.of(page, elementsPerPage));
        } else {
            existingTacheTermineEnRet = tacheService.getTaskTermineEnRet("", PageRequest.of(page, elementsPerPage));
        }
        model.addAttribute("task", existingTacheTermineEnRet.getContent());
        model.addAttribute("count",existingTacheTermineEnRet.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", existingTacheTermineEnRet.getTotalPages());
        model.addAttribute("search", search); // Add the search parameter to the model
        return "admin/tache-termineretard";
    }
    @PostMapping("/tache/save")
    public String saveUtilisateur(@ModelAttribute @Valid Tache tache, HttpServletRequest request, RedirectAttributes redirectAttributes , BindingResult bindingResult, Model model ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/tache";
        }
        // Retrieve the project's start and end dates
        List<Object[]> projectDatesList = projectRepository.findProjectDatesById(tache.getProject().getId());
        tache.setTitle(tache.getTitle().toLowerCase());
        tache.setDescription(tache.getDescription().toLowerCase());
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
        boolean NPexiste = tacheRepository.existsByTitleAndDescriptionAndProjectAndUsersNot(tache.getTitle().toLowerCase(), tache.getDescription().toLowerCase(),  tache.getProject(), tache.getUsers());
        if (NPexiste) {
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà.");
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

    @GetMapping("EditTache/{id}")
    public String getDetailTache(@PathVariable("id") Integer id, Model model){
        Tache tache = tacheService.getTacheById(id);
        model.addAttribute("tache", tache);
        List<Utilisateur> utilisateur = utilisateurService.getAllUtilisateurs();
        model.addAttribute("utilisateur", utilisateur);
        List<Project> project = projects.getAllProject();
        model.addAttribute("project",project);
        model.addAttribute("activePage","detailTache");
        return "admin/edit-tache";
    }

    @PostMapping("/tache/update")
    public String updateTache(@ModelAttribute("tache") Tache updateTache, RedirectAttributes redirectAttributes, Model model, BindingResult  bindingResult){
       Tache existingTache = tacheService.getTacheById(updateTache.getId());
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/EditTache/" + existingTache.getId();
        }
        boolean DejaExiste = tacheRepository.existsByTitleAndDescriptionAndDatedAndDatefAndUsersAndProjectAndEtat(updateTache.getTitle().toLowerCase(), updateTache.getDescription().toLowerCase(), updateTache.getDated(), updateTache.getDatef(), updateTache.getUsers(), updateTache.getProject(), updateTache.getEtat());
        if (DejaExiste) {
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà.");
            return "redirect:/EditTache/" + existingTache.getId();
        }
        boolean NPexiste = tacheRepository.existsByTitleAndDescriptionAndProjectAndUsersNot(updateTache.getTitle().toLowerCase(), updateTache.getDescription().toLowerCase(),  updateTache.getProject(), updateTache.getUsers());
        if (NPexiste) {
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà.");
            return "redirect:/EditTache/" + existingTache.getId();
        }
         if (existingTache.getEtat() == 0 && updateTache.getEtat() != 0) {
            existingTache.setDatedu(updateTache.getDatedu());
        }
         if (existingTache.getEtat() != 100 && updateTache.getEtat() == 100) {
             existingTache.setDatefu(updateTache.getDatedu());
         }
             existingTache.setTitle(updateTache.getTitle().toLowerCase());
             existingTache.setDescription(updateTache.getDescription().toLowerCase());
             existingTache.setDated(updateTache.getDated());
             existingTache.setDatef(updateTache.getDatef());
             existingTache.setEtat(updateTache.getEtat());
             existingTache.setProject(updateTache.getProject());
             existingTache.setUsers(updateTache.getUsers());

               tacheService.save(existingTache);
        return  "redirect:/EditTache/" + existingTache.getId();
    }

    @PostMapping("/tache/delete")
    public String tacheSupp(@ModelAttribute("tache") Tache tache, RedirectAttributes redirectAttributes, Model model, BindingResult bindingResult){
        Tache existingTache = tacheService.getTacheById(tache.getId());

        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/tache" ;
        }
        tacheService.deleteProjectById(tache.getId());

        tacheSupprimeeService.transferTacheSupprimee(existingTache);
        return "redirect:/tache";
    }




}
