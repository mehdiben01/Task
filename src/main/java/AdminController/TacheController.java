package AdminController;

import Model.Project;
import Model.Tache;
import Model.TacheSupprimee;
import Model.Utilisateur;
import Repository.ProjectRepository;
import Repository.TacheRepository;
import Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class TacheController {

    int elementsPerPage = 6;

    List<String> errors = new ArrayList<>();
    private final TacheRepository tacheRepository;

    private final TacheService tacheService;
    private final ProjectRepository projectRepository;

    private final TacheSupprimeeService tacheSupprimeeService;
    private final ImageService imageService;



    @Autowired
    public  TacheController(TacheRepository tacheRepository, TacheService tacheService, TacheSupprimeeService tacheSupprimeeService, ProjectRepository projectRepository, ImageService imageService, UtilisateurService utilisateurService, ProjectService projects){
        this.tacheRepository = tacheRepository;
        this.tacheService = tacheService;
        this.projectRepository = projectRepository;
        this.tacheSupprimeeService = tacheSupprimeeService;
        this.imageService = imageService;
        this.utilisateurService = utilisateurService;
        this.projects = projects;
    }


    private final UtilisateurService utilisateurService;

    private final ProjectService projects;


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
            existingTache = tacheService.getAllTask(search, Pageable.unpaged());
        } else {
            existingTache = tacheService.getAllTask("", PageRequest.of(page, elementsPerPage));
        }
        // Récupérez l'URL pré-signée pour chaque client
        for (Object[] clientData : existingTache) {
            String cheminImage = (String) clientData[2];
            if (cheminImage != null) {
                String privateImageURL = imageService.getPrivateImageURL("taskmanager", cheminImage);
                clientData[2] = privateImageURL; // Remplacez le chemin de l'image par l'URL pré-signée
            }
        }
        // Récupérez l'URL pré-signée pour chaque client
        for (Object[] clientData : existingTache) {
            String cheminImageCompany = (String) clientData[3];
            if (cheminImageCompany != null) {
                String privateImageURL = imageService.getPrivateImageURL("taskmanager", cheminImageCompany);
                clientData[3] = privateImageURL; // Remplacez le chemin de l'image par l'URL pré-signée
            }
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
            return "redirect:/admin/tache";
        }
        // Retrieve the project's start and end dates
        List<Object[]> projectDatesList = projectRepository.findProjectDatesById(tache.getProject().getId());
        tache.setTitle(tache.getTitle().toLowerCase());
        tache.setDescription(tache.getDescription().toLowerCase());
        Object[] projectDates = projectDatesList.get(0); // Retrieve the first row
        // Check if the task's start date is after the project's start date
        Date taskStartDate = tache.getDated();
        Date projectStartDate = (Date) projectDates[0]; // Assurez-vous que projectDates[0] est de type java.util.Date
        if (taskStartDate.before(projectStartDate)) {
            redirectAttributes.addFlashAttribute("errors", "Vérifier les dates du projet.");
            return "redirect:/admin/tache";
        }
        // Check if the task's end date is before the project's end date
        Date taskEndDate = tache.getDatef();
        Date projectEndDate = (Date) projectDates[1]; // Assurez-vous que projectDates[1] est de type java.util.Date
        if (taskEndDate.after(projectEndDate)) {
            redirectAttributes.addFlashAttribute("errors", "Vérifier les dates du projet.");
            return "redirect:/admin/tache";
        }
        boolean NPexiste = tacheRepository.existsByTitleAndDescriptionAndProjectAndUsersNot(tache.getTitle().toLowerCase(), tache.getDescription().toLowerCase(),  tache.getProject(), tache.getUsers());
        if (NPexiste) {
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà.");
            return "redirect:/admin/tache";
        }
        // Vérifier si l'utilisateur existe déjà
        boolean champExiste = tacheRepository.existsByTitleAndProjectAndUsers(tache.getTitle(), tache.getProject(), tache.getUsers());
        if (champExiste) {
            // Gérer le cas où l'utilisateur existe déjà
            redirectAttributes.addFlashAttribute("message", "La tache existe déjà.");
            return "redirect:/admin/tache";
        }
        // Enregistrer l'utilisateur en utilisant votre service
        redirectAttributes.addFlashAttribute("messagesu", "La tache a été ajouté avec succès.");
        tacheService.save(tache);
        return "redirect:/admin/tache";
    }

    @GetMapping("DetailTache/{id}")
    public String getDetailTache(@PathVariable("id") Integer id, Model model){
        Tache tache = tacheService.getTacheById(id);
        model.addAttribute("tache", tache);
        model.addAttribute("activePage","tache");
        String cheminImageMembre = tache.getUsers().getCheminImage();
        if (cheminImageMembre != null) {
            String privateImageURL = imageService.getPrivateImageURL("taskmanager", cheminImageMembre);
            model.addAttribute("UserImage", privateImageURL);
        }
        String cheminImageProject = tache.getProject().getClients().getCheminImage();
        if (cheminImageProject != null) {
            String privateImageURL = imageService.getPrivateImageURL("taskmanager", cheminImageProject);
            model.addAttribute("CompanyImage", privateImageURL);
        }
        return "admin/detail-tache";
    }

    @GetMapping("EditTache/{id}")
    public String getEditTache(@PathVariable("id") Integer id, Model model){
        Tache tache = tacheService.getTacheById(id);
        model.addAttribute("tache", tache);
        List<Utilisateur> utilisateur = utilisateurService.getAllUtilisateurs();
        model.addAttribute("utilisateur", utilisateur);
        List<Project> project = projects.getAllProject();
        model.addAttribute("project",project);
        model.addAttribute("activePage","tache");
        return "admin/edit-tache";
    }

    @PostMapping("/tache/update")
    public String updateTache(@ModelAttribute("tache") Tache updateTache, RedirectAttributes redirectAttributes, Model model, BindingResult  bindingResult){
       Tache existingTache = tacheService.getTacheById(updateTache.getId());
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/admin/EditTache/" + existingTache.getId();
        }
        boolean DejaExiste = tacheRepository.existsByTitleAndDescriptionAndDatedAndDatefAndUsersAndProjectAndEtat(updateTache.getTitle().toLowerCase(), updateTache.getDescription().toLowerCase(), updateTache.getDated(), updateTache.getDatef(), updateTache.getUsers(), updateTache.getProject(), updateTache.getEtat());
        if (DejaExiste) {
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà.");
            return "redirect:/admin/EditTache/" + existingTache.getId();
        }
        boolean NPexiste = tacheRepository.existsByTitleAndDescriptionAndProjectAndUsersNot(updateTache.getTitle().toLowerCase(), updateTache.getDescription().toLowerCase(),  updateTache.getProject(), updateTache.getUsers());
        if (NPexiste) {
            redirectAttributes.addFlashAttribute("message", "Donnée existe déjà.");
            return "redirect:/admin/EditTache/" + existingTache.getId();
        }
         if (existingTache.getEtat() == 0 && updateTache.getEtat() != 0) {
            existingTache.setDatedu(updateTache.getDatedu());
        }
         if (existingTache.getEtat() != 100 && updateTache.getEtat() == 100) {
             existingTache.setDatefu(updateTache.getDatedu());
         }
        if (updateTache.getEtat() == 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = dateFormat.parse("0001-01-01");
            } catch (ParseException e) {
                // Gérer l'exception si la date ne peut pas être analysée
                e.printStackTrace();
            }
            existingTache.setDatedu(date);
        }
             existingTache.setTitle(updateTache.getTitle().toLowerCase());
             existingTache.setDescription(updateTache.getDescription().toLowerCase());
             existingTache.setDated(updateTache.getDated());
             existingTache.setDatef(updateTache.getDatef());
             existingTache.setEtat(updateTache.getEtat());
             existingTache.setProject(updateTache.getProject());
             existingTache.setUsers(updateTache.getUsers());
             tacheService.save(existingTache);
        return  "redirect:/admin/EditTache/" + existingTache.getId();
    }

    @PostMapping("/tache/delete")
    public String TacheSupp(@ModelAttribute("tache") Tache tache, RedirectAttributes redirectAttributes, Model model, BindingResult bindingResult){
        Tache existingTache = tacheService.getTacheById(tache.getId());

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/admin/tache" ;
        }
        tacheService.deleteTacheById(tache.getId());

        tacheSupprimeeService.transferTacheSupprimee(existingTache);
        return "redirect:/admin/tache";
    }

    @PostMapping("/tache/recup")
    public String TacheRecup(@ModelAttribute("tache") TacheSupprimee tacheSupprimee, RedirectAttributes redirectAttributes, Model model, BindingResult bindingResult){
        TacheSupprimee existingTachesupp = tacheSupprimeeService.getTachesuppById(tacheSupprimee.getId());

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/admin/tache-annule" ;
        }

        tacheSupprimeeService.deleteTacheSuppById(tacheSupprimee.getId());

        tacheService.transferTache(existingTachesupp);
        return "redirect:/admin/tache-annule";
    }
}
