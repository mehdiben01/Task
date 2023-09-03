package UserController;

import Model.Tache;
import Model.Utilisateur;
import Repository.ProjectRepository;
import Repository.TacheRepository;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/user")
public class TacheUController {
    int elementsPerPage = 6;

    List<String> errors = new ArrayList<>();
    private final TacheRepository tacheRepository;

    private final TacheService tacheService;
    private final ProjectRepository projectRepository;

    private final TacheSupprimeeService tacheSupprimeeService;

    private final UtilisateurService utilisateurService;

    private final ProjectService projects;



    @Autowired
    public  TacheUController(TacheRepository tacheRepository, TacheService tacheService, TacheSupprimeeService tacheSupprimeeService, ProjectRepository projectRepository, UtilisateurService utilisateurService, ProjectService projects){
        this.tacheRepository = tacheRepository;
        this.tacheService = tacheService;
        this.projectRepository = projectRepository;
        this.tacheSupprimeeService = tacheSupprimeeService;
        this.utilisateurService = utilisateurService;
        this.projects = projects;
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
                model.addAttribute("profession", utilisateur.getProfession());
                model.addAttribute("imagePath", utilisateur.getCheminImage());
                // You can add more attributes here
            }
        }
    }

    @GetMapping("/tacheu")
    public String getTacheUser(Model model, @RequestParam(required = false) String search,
                               @RequestParam(defaultValue = "0") int page){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Utilisateur utilisateur = utilisateurService.loadUserByUsername(username);
        Tache tache = new Tache();
        model.addAttribute("tache",tache);
        model.addAttribute("users",utilisateurService.getAllUtilisateurs() );
        model.addAttribute("projects", projects.getAllProject());
        model.addAttribute("activePage", "tacheu");
        Page<Object[]> existingTacheUser;
        if (search != null && !search.isEmpty()) {
            existingTacheUser = tacheService.getAllTaskByUser(utilisateur.getId(),search, PageRequest.of(page, elementsPerPage));
        } else {
            existingTacheUser = tacheService.getAllTaskByUser(utilisateur.getId(),"", PageRequest.of(page, elementsPerPage));
        }
        model.addAttribute("task", existingTacheUser.getContent());
        model.addAttribute("count",existingTacheUser.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", existingTacheUser.getTotalPages());
        model.addAttribute("search", search); // Add the search parameter to the model
        return "staff/tacheu";
    }
    @GetMapping("/DetailTacheU/{id}")
    public String GetTacheU(@PathVariable("id") Integer id, Model model) {
        Tache tache = tacheService.getTacheById(id);

        if (tache == null) {
            // Gérer le cas où la tâche n'existe pas, rediriger ou afficher une page d'erreur appropriée
            return "redirect:/error"; // Par exemple, rediriger vers une page d'erreur générale
        }

        Date currentDate = new Date(); // Obtenez la date actuelle en tant qu'objet java.util.Date
        Date endDate = tache.getDatef(); // Supposons que tache.getDatef() renvoie un objet java.util.Date

        if (endDate.before(currentDate) && tache.getEtat() != 100) {
            // La date de fin est antérieure à la date actuelle, rediriger vers la page d'accès refusé
            return "redirect:/denied";
        }


        model.addAttribute("tache", tache);
        model.addAttribute("activePage", "tacheu");
        return "staff/detail-tache";
    }


    @PostMapping("/tacheu/update")
    public String updateTache(@ModelAttribute("tache") Tache updateTache, Model model, RedirectAttributes redirectAttributes){
        Tache existingTacheU = tacheService.getTacheById(updateTache.getId());
        if (existingTacheU.getEtat() == 0 && updateTache.getEtat() != 0) {
            existingTacheU.setDatedu(updateTache.getDatedu());
        }
        if (existingTacheU.getEtat() != 100 && updateTache.getEtat() == 100) {
            existingTacheU.setDatefu(updateTache.getDatedu());
        }
        existingTacheU.setEtat(updateTache.getEtat());
        tacheService.save(existingTacheU);
        return "redirect:/user/DetailTacheU/" + existingTacheU.getId();
    }

    @GetMapping("/tacheu-termine")
    public String getTacheUserTermine(Model model, @RequestParam(required = false) String search,
                               @RequestParam(defaultValue = "0") int page){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Utilisateur utilisateur = utilisateurService.loadUserByUsername(username);
        Tache tache = new Tache();
        model.addAttribute("tache",tache);
        model.addAttribute("users",utilisateurService.getAllUtilisateurs() );
        model.addAttribute("projects", projects.getAllProject());
        model.addAttribute("activePage", "tacheu-termine");
        Page<Object[]> existingTacheUser;
        if (search != null && !search.isEmpty()) {
            existingTacheUser = tacheService.getAllTaskTermineByUser(utilisateur.getId(),search, PageRequest.of(page, elementsPerPage));
        } else {
            existingTacheUser = tacheService.getAllTaskTermineByUser(utilisateur.getId(),"", PageRequest.of(page, elementsPerPage));
        }
        model.addAttribute("task", existingTacheUser.getContent());
        model.addAttribute("count",existingTacheUser.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", existingTacheUser.getTotalPages());
        model.addAttribute("search", search); // Add the search parameter to the model
        return "staff/tacheu-termine";
    }

    @GetMapping("/tacheu-encours")
    public String getTacheUserEnCours(Model model, @RequestParam(required = false) String search,
                                      @RequestParam(defaultValue = "0") int page){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Utilisateur utilisateur = utilisateurService.loadUserByUsername(username);
        Tache tache = new Tache();
        model.addAttribute("tache",tache);
        model.addAttribute("users",utilisateurService.getAllUtilisateurs() );
        model.addAttribute("projects", projects.getAllProject());
        model.addAttribute("activePage", "tacheu-encours");
        Page<Object[]> existingTacheUser;
        if (search != null && !search.isEmpty()) {
            existingTacheUser = tacheService.getAllTaskEnCoursByUser(utilisateur.getId(),search, PageRequest.of(page, elementsPerPage));
        } else {
            existingTacheUser = tacheService.getAllTaskEnCoursByUser(utilisateur.getId(),"", PageRequest.of(page, elementsPerPage));
        }
        model.addAttribute("task", existingTacheUser.getContent());
        model.addAttribute("count",existingTacheUser.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", existingTacheUser.getTotalPages());
        model.addAttribute("search", search); // Add the search parameter to the model
        return "staff/tacheu-encours";
    }

    @GetMapping("/tacheu-noncommence")
    public String getTacheUserNonCommence(Model model, @RequestParam(required = false) String search,
                                      @RequestParam(defaultValue = "0") int page){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Utilisateur utilisateur = utilisateurService.loadUserByUsername(username);
        Tache tache = new Tache();
        model.addAttribute("tache",tache);
        model.addAttribute("users",utilisateurService.getAllUtilisateurs() );
        model.addAttribute("projects", projects.getAllProject());
        model.addAttribute("activePage", "tacheu-noncommence");
        Page<Object[]> existingTacheUser;
        if (search != null && !search.isEmpty()) {
            existingTacheUser = tacheService.getAllTaskNonCommenceByUser(utilisateur.getId(),search, PageRequest.of(page, elementsPerPage));
        } else {
            existingTacheUser = tacheService.getAllTaskNonCommenceByUser(utilisateur.getId(),"", PageRequest.of(page, elementsPerPage));
        }
        model.addAttribute("task", existingTacheUser.getContent());
        model.addAttribute("count",existingTacheUser.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", existingTacheUser.getTotalPages());
        model.addAttribute("search", search); // Add the search parameter to the model
        return "staff/tacheu-noncommence";
    }

}
