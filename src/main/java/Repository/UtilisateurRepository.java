package Repository;

import Model.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    boolean existsByEmailOrTel( String email, String tel);

    boolean existsByNomAndPrenomAndProfessionAndDatenAndTelAndType(String nom,  String prenom, String profession, String daten, String tel, String type);

    boolean existsByNomAndPrenomAndIdNot(String nom,String prenom, Integer id);

    boolean existsByTelAndIdNot(String tel , Integer id);

    boolean existsByPassword(String password);

  boolean existsByNomAndPrenom(String nom, String prenom);
    List<Utilisateur> findAllByIsDeletedAndType(String isDeleted, String type);

    Page<Utilisateur> findAllByIsDeletedAndType(String isDeleted, String type, Pageable pageable);

    long countByTypeAndIsDeleted(String type , String isDeleted);


    Optional<Utilisateur> getUtilisateurById(Integer id);



}
