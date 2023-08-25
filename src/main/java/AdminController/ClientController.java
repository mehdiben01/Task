package AdminController;

import Model.Client;
import Model.Utilisateur;
import Repository.ClientRepository;
import Service.ClientService;
import Service.ImageService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class ClientController {

    int elementsPerPage = 6;

    private final ClientRepository clientRepository;

    private final ClientService clientService;

    private final ClientService service;

    private final UtilisateurService utilisateurService;
    private final ImageService imageService;

    @Autowired
    public ClientController(ClientRepository clientRepository, ClientService clientService, ClientService service, UtilisateurService utilisateurService, ImageService imageService) {
        this.clientRepository = clientRepository;
        this.clientService = clientService;
        this.service = service;
        this.utilisateurService = utilisateurService;
        this.imageService = imageService;
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


    @GetMapping("/client")
    public String getClient(Model model, @RequestParam(required = false) String search,
                            @RequestParam(defaultValue = "0") int page){
        Client client = new Client();
        model.addAttribute("client", client);
        Page<Object[]> existingClients;
        if (search != null && !search.isEmpty()) {
            existingClients = clientService.getAllClients(search, PageRequest.of(page, elementsPerPage));
        } else {
            existingClients = clientService.getAllClients("", PageRequest.of(page, elementsPerPage));
        }
        model.addAttribute("clients", existingClients.getContent());
        model.addAttribute("countclient", existingClients.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("activePage", "client");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", existingClients.getTotalPages());
        return "admin/client";
    }
    @GetMapping("/client-annule")
    public String getClientSupp(Model model, @RequestParam(required = false) String search,
                            @RequestParam(defaultValue = "0") int page){
        Client client = new Client();
        model.addAttribute("client", client);
        Page<Object[]> existingClientSupp;
        if (search != null && !search.isEmpty()) {
            existingClientSupp = clientService.getAllClientsSupp(search, PageRequest.of(page, elementsPerPage));
        } else {
            existingClientSupp = clientService.getAllClientsSupp("", PageRequest.of(page, elementsPerPage));
        }
        model.addAttribute("clients", existingClientSupp.getContent());
        model.addAttribute("countclient", existingClientSupp.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("activePage", "client-annule");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", existingClientSupp.getTotalPages());
        return "admin/client-annule";
    }


    @PostMapping("/client/save")
    public String saveClient(@ModelAttribute @Valid Client client, @RequestParam("logo") MultipartFile imageFile, HttpServletRequest request, RedirectAttributes redirectAttributes , BindingResult bindingResult, Model model ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/client";
        }
        client.setNom(client.getNom().toUpperCase());
        client.setCompany(client.getCompany().toUpperCase());
        client.setEmail(client.getEmail().toLowerCase());
        client.setDepartement(client.getDepartement().toLowerCase());
        // Vérifier si l'utilisateur existe déjà
        boolean champExiste = clientRepository.existsByEmailOrTelOrCompany(client.getEmail().toLowerCase(), client.getTel(), client.getCompany().toUpperCase());
        if (champExiste) {
            // Gérer le cas où l'utilisateur existe déjà
            redirectAttributes.addFlashAttribute("message", "Client existe déjà.");
            return "redirect:/admin/client";
        }
        boolean NomExiste = clientRepository.existsByNomAndPrenomAndIdNot(client.getNom().toUpperCase(), client.getPrenom().toLowerCase(), client.getId());
        if (NomExiste) {
            // Gérer le cas où l'utilisateur existe déjà
            redirectAttributes.addFlashAttribute("message", "Nom existe déjà.");
            return "redirect:/admin/client";
        }
        if (!imageFile.isEmpty()) {
            boolean imageUpdateResult = imageService.updateImage(client, imageFile);
            if (!imageUpdateResult) {
                redirectAttributes.addFlashAttribute("message", ImageService.IMAGE_SIZE_ERROR_MESSAGE);
                return "redirect:/admin/team";
            }
        }

        // Enregistrer l'utilisateur en utilisant votre service
        redirectAttributes.addFlashAttribute("messagesu", "le client a été ajouté avec succès.");
        service.save(client);

        return "redirect:/admin/client";
    }

    @GetMapping("/EditClient/{id}")
    public String getDetailClient(@PathVariable("id") Integer id, Model model){
        Client client = clientService.getClientById(id);
        model.addAttribute("clients", client);
        Client clients = new Client();
        model.addAttribute("client", clients);
        model.addAttribute("activePage", "detailClient");
        return "admin/edit-client";
    }

    @PostMapping("/client/update")
    public String updateClient(@ModelAttribute("client") Client updateclient , RedirectAttributes redirectAttributes, BindingResult bindingResult, Model model){
    // Récupérer l'utilisateur existant à partir de la base de données
        Client existingClient = clientService.getClientById(updateclient.getId());
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/admin/EditClient/" + existingClient.getId();
        }

        // Vérifier si l'utilisateur existe déjà
        boolean ClientExiste = clientRepository.existsByNomAndPrenomAndEmailAndTelAndCompany(updateclient.getNom().toUpperCase(),updateclient.getPrenom().toLowerCase(),updateclient.getEmail(),updateclient.getTel(),updateclient.getCompany().toUpperCase());
        if (ClientExiste) {
            // Gérer le cas où l'utilisateur existe déjà
            redirectAttributes.addFlashAttribute("message", "Les données existent déjà.");
            return "redirect:/admin/EditClient/" + existingClient.getId();
        }else {

            existingClient.setNom(updateclient.getNom().toUpperCase());
            existingClient.setPrenom(updateclient.getPrenom().toLowerCase());
            existingClient.setTel(updateclient.getTel());
            existingClient.setCompany(updateclient.getCompany().toUpperCase());
        }

        boolean NomExiste = clientRepository.existsByNomAndPrenomAndIdNot(updateclient.getNom().toUpperCase(), updateclient.getPrenom().toLowerCase(), updateclient.getId());
        if (NomExiste) {
            // Gérer le cas où l'utilisateur existe déjà
            redirectAttributes.addFlashAttribute("message", "Nom existe déjà.");
            return "redirect:/admin/EditClient/" + existingClient.getId();
        }

        boolean TelExiste = clientRepository.existsByTelAndIdNot(updateclient.getTel(), updateclient.getId());
        if (TelExiste) {
            // Gérer le cas où l'utilisateur existe déjà
            redirectAttributes.addFlashAttribute("message", "Tel existe déjà.");
            return "redirect:/admin/EditClient/" + existingClient.getId();
        }

        boolean CompnayExiste = clientRepository.existsByCompanyAndIdNot(updateclient.getCompany().toUpperCase(), updateclient.getId());
        if (CompnayExiste) {
            // Gérer le cas où l'utilisateur existe déjà
            redirectAttributes.addFlashAttribute("message", "Entreprise existe déjà.");
            return "redirect:/admin/EditClient/" + existingClient.getId();
        }



        redirectAttributes.addFlashAttribute("messagepro", "Les données ont été mises à jour avec succès.");
        // Enregistrer les modifications dans la base de données
        clientService.save(existingClient);

        return "redirect:/admin/EditClient/" + existingClient.getId();

    }


    @PostMapping("/update/imagec")
    public String updateImageUser(@ModelAttribute("client") @Valid Client updateClient, @RequestParam("image") MultipartFile imageFile , RedirectAttributes redirectAttributes, BindingResult bindingResult, Model model) {

        // Récupérer l'utilisateur existant à partir de la base de données
        Client existingClient = clientService.getClientById(updateClient.getId());

        // Vérifier si un nouveau fichier d'image a été sélectionné
        boolean updateSuccess = imageService.updateImage(existingClient, imageFile);


        if (imageFile.getSize() > 2 * 1024 * 1024) {
            redirectAttributes.addFlashAttribute("message", ImageService.IMAGE_SIZE_ERROR_MESSAGE);
        } else if (!updateSuccess) {
            redirectAttributes.addFlashAttribute("message", ImageService.IMAGE_SAVE_ERROR_MESSAGE);
        }
        else {
            redirectAttributes.addFlashAttribute("messagepro", "Votre photo de profil a été mise à jour avec succès.");
            clientService.save(existingClient);
        }

        return "redirect:/admin/EditClient/" + existingClient.getId();
    }

    @PostMapping("/client/delete")
    public String DeleteClient(@ModelAttribute("clilent") Client updateClient , RedirectAttributes redirectAttributes, BindingResult bindingResult, Model model  ) {

        // Récupérer l'utilisateur existant à partir de la base de données
        Client existingClient = clientService.getClientById(updateClient.getId());

        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/admin/EditClient/" + existingClient.getId();
        }

        redirectAttributes.addFlashAttribute("messagesu", "Le compte a été fermé avec succès.");
        // Mettre à jour les attributs de l'utilisateur existant avec les nouvelles valeurs
        existingClient.setIsDeleted("1");

        // Enregistrer les modifications dans la base de données
        clientService.save(existingClient);

        return "redirect:/admin/client";
    }

    @PostMapping("/client/recup")
    public String RecupClient(@ModelAttribute("client") Client updateClient , RedirectAttributes redirectAttributes, BindingResult bindingResult, Model model  ) {

        // Récupérer l'utilisateur existant à partir de la base de données
        Client existingClient = clientService.getClientById(updateClient.getId());

        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Il y a des erreurs de validation.");
            return "redirect:/admin/client-annule";
        }

        redirectAttributes.addFlashAttribute("messagesu", "Le compte a été récupéré avec succès.");
        // Mettre à jour les attributs de l'utilisateur existant avec les nouvelles valeurs
        existingClient.setIsDeleted("0");

        // Enregistrer les modifications dans la base de données
        clientService.save(existingClient);

        return "redirect:/admin/client-annule";
    }


}
