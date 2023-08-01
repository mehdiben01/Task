package Repository;

import Model.Project;
import Model.Tache;
import Model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface TacheRepository extends JpaRepository<Tache, Integer> {

    boolean existsByTitleAndProjectAndUsers(String title, Project project, Utilisateur users );

    boolean existsByTitleAndDescriptionAndProjectAndUsersNot(String title,String description, Project project, Utilisateur users );

    boolean existsByTitleAndDescriptionAndDatedAndDatefAndUsersAndProjectAndEtat(String title, String description, String dated, String datef,  Utilisateur users, Project project, Integer etat);
    List<Tache> findAllByIsDeleted(String isDeleted);

    long countByIsDeleted(String isDeleted);

    Optional<Tache> getTacheById(Integer id);


    @Query("SELECT COUNT(p) " +
            "FROM Project p " +
            "WHERE p.isDeleted = '0' " +
            "AND TO_DATE(p.datef, 'YYYY-MM-DD') > CURRENT_DATE " +
            "AND p.id IN (SELECT t.project.id " +
            "FROM Tache t " +
            "GROUP BY t.project.id " +
            "HAVING AVG(t.etat) = 100)")
    long countPTermine();

    @Query("SELECT COUNT(p) " +
            "FROM Project p " +
            "WHERE p.isDeleted = '0' " +
            "AND TO_DATE(p.datef, 'YYYY-MM-DD') > CURRENT_DATE " +
            "AND p.id IN (SELECT t.project.id " +
            "FROM Tache t " +
            "GROUP BY t.project.id " +
            "HAVING AVG(t.etat) between 1 and 99)")
    long countENC();

    @Query("SELECT COUNT(p.id) " +
            "FROM Project p " +
            "WHERE TO_DATE(p.datef, 'YYYY-MM-DD') > CURRENT_DATE " +
            "AND p.isDeleted = '0' " +
            "AND not exists ( " +
            "SELECT t.project.id " +
            "FROM Tache t " +
            "WHERE t.project.id = p.id " +
    ")")
    long countNonC();







}
