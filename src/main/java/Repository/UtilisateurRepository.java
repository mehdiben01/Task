package Repository;

import Model.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {

    Utilisateur findByUsername(String username);
    boolean existsByUsernameOrTel( String username, String tel);



    boolean existsByNomAndPrenomAndProfessionAndDatenAndTel(String nom,  String prenom, String profession, String daten, String tel);

    boolean existsByNomAndPrenomAndIdNot(String nom,String prenom, Integer id);

    boolean existsByTelAndIdNot(String tel , Integer id);

    boolean existsByPassword(String password);

  boolean existsByNomAndPrenom(String nom, String prenom);
    List<Utilisateur> findAllByIsDeleted(String isDeleted);

    Page<Utilisateur> findAllByIsDeleted(String isDeleted,  Pageable pageable);

    long countByIsDeleted(String isDeleted);


    Optional<Utilisateur> getUtilisateurById(Integer id);


    @Query("SELECT u , count(u.id) "+
            "FROM Utilisateur u "+
            "JOIN u.roles r " +
            "WHERE u.isDeleted ='0' "+
            "AND r.role = 'USER' " +
            "AND (LOWER(u.nom) LIKE %:search% "+
            "OR LOWER(u.prenom) LIKE %:search% "+
            "OR LOWER(u.tel) LIKE %:search%" +
            "OR LOWER(u.username) LIKE %:search%" +
            "OR LOWER(u.profession) LIKE %:search%) " +
            "GROUP BY u.id ")
    Page<Object[]> findAllUsers(@Param("search") String search, Pageable pageable);

    @Query("SELECT u , count(u.id) "+
            "FROM Utilisateur u "+
            "WHERE u.isDeleted ='1' "+
            "AND (LOWER(u.nom) LIKE %:search% "+
            "OR LOWER(u.prenom) LIKE %:search% "+
            "OR LOWER(u.tel) LIKE %:search%" +
            "OR LOWER(u.username) LIKE %:search%" +
            "OR LOWER(u.profession) LIKE %:search%) " +
            "GROUP BY u.id ")
    Page<Object[]> findAllUserSupp(@Param("search") String search, Pageable pageable);



}
