package Service;

import Model.Utilisateur;
import Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public Utilisateur save(Utilisateur utilisateur){
        return utilisateurRepository.save(utilisateur);
    }
    // Méthode pour récupérer tous les utilisateurs where type = staff
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAllByIsDeletedAndType("0", "Staff");
    }

    public long countStaff(){
        return utilisateurRepository.countByTypeAndIsDeleted("Staff", "0");
    }
    public Utilisateur getUtilisateurById(Integer id) {
        return utilisateurRepository.getUtilisateurById(id)
                .orElseThrow(() -> new NoSuchElementException("Utilisateur not found with ID: " + id));
    }





}
