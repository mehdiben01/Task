package Service;

import Model.Utilisateur;
import Repository.UtilisateurRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UtilisateurService  {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public Utilisateur findByEmail(String username){
        return utilisateurRepository.findByUsername(username);
    }



    public Utilisateur save(Utilisateur utilisateur){
        return utilisateurRepository.save(utilisateur);
    }

    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAllByIsDeleted("0" );
    }

    public Page<Utilisateur> getAllUsersPagi(Pageable pageable){
        return utilisateurRepository.findAllByIsDeleted("0", pageable);
    }

    public long countStaff(){
        return utilisateurRepository.countByIsDeleted( "0");
    }
    public Utilisateur getUtilisateurById(Integer id) {
        return utilisateurRepository.getUtilisateurById(id)
                .orElseThrow(() -> new NoSuchElementException("Utilisateur not found with ID: " + id));
    }

    public Page<Object[]> getUtilisateur(String search, Pageable pageable){
        return utilisateurRepository.findAllUsers(search,pageable);
    }

    public Page<Object[]> getUtilisateurSupp(String search, Pageable pageable){
        return utilisateurRepository.findAllUserSupp(search,pageable);
    }

    public Utilisateur loadUserByUsername(String username){
        return utilisateurRepository.findByUsername(username);
    }

    public boolean existsByTelAndIdNot(String nom, Integer id){
        return utilisateurRepository.existsByTelAndIdNot(nom,id);
    }

    public boolean existsByNomAndPrenomAndProfessionAndDatenAndTel(String nom, String prenom , String profession , Date daten , String tel){
        return utilisateurRepository.existsByNomAndPrenomAndProfessionAndDatenAndTel(nom,prenom,profession,daten,tel);
    }

    public boolean existsByNomAndPrenomAndIdNot(String nom, String prenom , Integer id){
        return utilisateurRepository.existsByNomAndPrenomAndIdNot(nom,prenom,id);
    }



}
