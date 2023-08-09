package Repository;

import Model.Project;
import Model.Tache;
import Model.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface TacheRepository extends JpaRepository<Tache, Integer> {

    boolean existsByTitleAndProjectAndUsers(String title, Project project, Utilisateur users );

    boolean existsByTitleAndDescriptionAndProjectAndUsersNot(String title,String description, Project project, Utilisateur users );

    boolean existsByTitleAndDescriptionAndDatedAndDatefAndUsersAndProjectAndEtat(String title, String description, String dated, String datef,  Utilisateur users, Project project, Integer etat);



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


    @Query("SELECT t , count(t.id) , ts " +
            "FROM Tache t  , TacheSupprimee ts " +
            "WHERE (LOWER(t.title) LIKE %:search% " +
            "OR (LOWER(t.description)) LIKE %:search% " +
            "OR (LOWER(t.dated)) LIKE %:search% " +
            "OR (LOWER(t.datef)) LIKE %:search%) " +
            "GROUP BY t.id , ts.id ")
    Page<Object[]> selectAllTaches(@Param("search") String search, Pageable pageable);

    @Query("SELECT t , count(t.id) " +
            "FROM Tache t " +
            "WHERE t.datef > t.datefu " +
            "AND (LOWER(t.title) LIKE %:search% " +
            "OR (LOWER(t.description)) LIKE %:search% " +
            "OR (LOWER(t.dated)) LIKE %:search% " +
            "OR (LOWER(t.datef)) LIKE %:search% )" +
            "GROUP BY t.id ")
    Page<Object[]> selectAllTachesAnnuler(@Param("search") String search, Pageable pageable);

    @Query("SELECT t , count(t.id) " +
            "FROM Tache t " +
            "WHERE t.etat between 1 and 99 " +
            "AND t.datef >= t.datefu " +
            "AND TO_DATE(t.datef, 'YYYY-MM-DD') >= CURRENT_DATE " +
            "AND (LOWER(t.title) LIKE %:search% " +
            "OR (LOWER(t.description)) LIKE %:search% " +
            "OR (LOWER(t.dated)) LIKE %:search% " +
            "OR (LOWER(t.datef)) LIKE %:search% )" +
            "GROUP BY t.id ")
    Page<Object[]> selectAllTachesEncours(@Param("search") String search, Pageable pageable);

    @Query("SELECT t , count(t.id) " +
            "FROM Tache t " +
            "WHERE t.etat = 0 " +
            "AND t.datef >= t.datefu " +
            "AND (LOWER(t.title) LIKE %:search% " +
            "OR (LOWER(t.description)) LIKE %:search% " +
            "OR (LOWER(t.dated)) LIKE %:search% " +
            "OR (LOWER(t.datef)) LIKE %:search% )" +
            "GROUP BY t.id ")
    Page<Object[]> selectAllTachesNONC(@Param("search") String search, Pageable pageable);

    @Query("SELECT t , count(t.id) " +
            "FROM Tache t " +
            "WHERE t.etat != 100 " +
            "AND (t.datef < t.datefu) or TO_DATE(t.datef, 'YYYY-MM-DD') < CURRENT_DATE " +
            "AND (LOWER(t.title) LIKE %:search% " +
            "OR (LOWER(t.description)) LIKE %:search% " +
            "OR (LOWER(t.dated)) LIKE %:search% " +
            "OR (LOWER(t.datef)) LIKE %:search% )" +
            "GROUP BY t.id ")
    Page<Object[]> selectAllTachesRet(@Param("search") String search, Pageable pageable);

    @Query("SELECT t , count(t.id) " +
            "FROM Tache t " +
            "WHERE t.etat = 100 " +
            "AND t.datef >= t.datefu " +
            "AND (LOWER(t.title) LIKE %:search% " +
            "OR (LOWER(t.description)) LIKE %:search% " +
            "OR (LOWER(t.dated)) LIKE %:search% " +
            "OR (LOWER(t.datef)) LIKE %:search% )" +
            "GROUP BY t.id ")
    Page<Object[]> selectAllTachesTermine(@Param("search") String search, Pageable pageable);

    @Query("SELECT t , count(t.id) " +
            "FROM Tache t " +
            "WHERE t.etat = 100 " +
            "AND t.datef < t.datefu " +
            "AND (LOWER(t.title) LIKE %:search% " +
            "OR (LOWER(t.description)) LIKE %:search% " +
            "OR (LOWER(t.dated)) LIKE %:search% " +
            "OR (LOWER(t.datef)) LIKE %:search% ) " +
            "GROUP BY t.id ")
    Page<Object[]> selectAllTachesTermineEnRet(@Param("search") String search, Pageable pageable);

    void deleteTacheById(Integer id);






}
