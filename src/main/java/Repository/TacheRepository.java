package Repository;

import Model.Project;
import Model.Tache;
import Model.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface TacheRepository extends JpaRepository<Tache, Integer> {

    /* ---------------------- TacheRepository ADMIN ---------------------- */

    boolean existsByTitleAndProjectAndUsers(String title, Project project, Utilisateur users );

    boolean existsByTitleAndDescriptionAndProjectAndUsersNot(String title,String description, Project project, Utilisateur users );

    boolean existsByTitleAndDescriptionAndDatedAndDatefAndUsersAndProjectAndEtat(String title, String description, Date dated, Date datef, Utilisateur users, Project project, Integer etat);

    Optional<Tache> getTacheById(Integer id);

    @Query("Select t "+
           "From Tache t "+
            "where t.dated<t.datedu order by t.dated ")
    List<Tache> selectTacheRetDebut();


    @Query("SELECT t , COUNT(t.id) , t.users.cheminImage, t.project.clients.cheminImage " +
            "FROM Tache t " +
            "WHERE (LOWER(t.title) LIKE %:search% " +
            "OR (LOWER(t.description)) LIKE %:search% " +
            "OR TO_CHAR(t.dated, 'YYYY-MM-DD') LIKE %:search% " +
            "OR TO_CHAR(t.datef, 'YYYY-MM-DD') LIKE %:search%) " +
            "GROUP BY t.id , t.users.cheminImage , t.project.clients.cheminImage   ")
    Page<Object[]> selectAllTaches(@Param("search") String search, Pageable pageable);

    @Query("SELECT t , count(t.id) " +
            "FROM Tache t " +
            "WHERE t.datef > t.datefu " +
            "AND (LOWER(t.title) LIKE %:search% " +
            "OR (LOWER(t.description)) LIKE %:search% " +
            "OR TO_CHAR(t.dated, 'YYYY-MM-DD') LIKE %:search% " +
            "OR TO_CHAR(t.datef, 'YYYY-MM-DD') LIKE %:search% )" +
            "GROUP BY t.id ")
    Page<Object[]> selectAllTachesAnnuler(@Param("search") String search, Pageable pageable);

    @Query("SELECT t , count(t.id) " +
            "FROM Tache t " +
            "WHERE t.etat between 1 and 99 " +
            "AND t.datef >= t.datefu " +
            "AND t.datef >= CURRENT_DATE " +
            "AND (LOWER(t.title) LIKE %:search% " +
            "OR (LOWER(t.description)) LIKE %:search% " +
            "OR TO_CHAR(t.dated, 'YYYY-MM-DD') LIKE %:search% " +
            "OR TO_CHAR(t.datef, 'YYYY-MM-DD') LIKE %:search% )" +
            "GROUP BY t.id ")
    Page<Object[]> selectAllTachesEncours(@Param("search") String search, Pageable pageable);

    @Query("SELECT t , count(t.id) " +
            "FROM Tache t " +
            "WHERE t.etat = 0 " +
            "AND t.datef >= t.datefu " +
            "AND t.datef >= CURRENT_DATE " +
            "AND (LOWER(t.title) LIKE %:search% " +
            "OR (LOWER(t.description)) LIKE %:search% " +
            "OR TO_CHAR(t.dated, 'YYYY-MM-DD') LIKE %:search% " +
            "OR TO_CHAR(t.datef, 'YYYY-MM-DD') LIKE %:search% )" +
            "GROUP BY t.id ")
    Page<Object[]> selectAllTachesNONC(@Param("search") String search, Pageable pageable);

    @Query("SELECT t , count(t.id) " +
            "FROM Tache t " +
            "WHERE t.etat != 100 " +
            "AND ((t.datef < t.datefu) or (t.datef < CURRENT_DATE)) " +
            "AND (LOWER(t.title) LIKE %:search% " +
            "OR (LOWER(t.description)) LIKE %:search% " +
            "OR TO_CHAR(t.dated, 'YYYY-MM-DD') LIKE %:search% " +
            "OR TO_CHAR(t.datef, 'YYYY-MM-DD') LIKE %:search% )" +
            "GROUP BY t.id ")
    Page<Object[]> selectAllTachesRet(@Param("search") String search, Pageable pageable);

    @Query("SELECT t , count(t.id) " +
            "FROM Tache t " +
            "WHERE t.etat = 100 " +
            "AND t.datef >= t.datefu " +
            "AND (LOWER(t.title) LIKE %:search% " +
            "OR (LOWER(t.description)) LIKE %:search% " +
            "OR TO_CHAR(t.dated, 'YYYY-MM-DD') LIKE %:search% " +
            "OR TO_CHAR(t.datef, 'YYYY-MM-DD') LIKE %:search% )" +
            "GROUP BY t.id ")
    Page<Object[]> selectAllTachesTermine(@Param("search") String search, Pageable pageable);

    @Query("SELECT t , count(t.id) " +
            "FROM Tache t " +
            "WHERE t.etat = 100 " +
            "AND t.datef < t.datefu " +
            "AND (LOWER(t.title) LIKE %:search% " +
            "OR (LOWER(t.description)) LIKE %:search% " +
            "OR TO_CHAR(t.dated, 'YYYY-MM-DD') LIKE %:search% " +
            "OR TO_CHAR(t.datef, 'YYYY-MM-DD') LIKE %:search% ) " +
            "GROUP BY t.id ")
    Page<Object[]> selectAllTachesTermineEnRet(@Param("search") String search, Pageable pageable);

    void deleteTacheById(Integer id);

    @Query("SELECT COUNT(t) " +
            "FROM Tache t " +
            "WHERE t.etat = 100 "+
            "AND t.datef >= t.datefu")
    long countT();

    @Query("SELECT COUNT(t) " +
            "FROM Tache t " +
            "WHERE t.datef >= CURRENT_DATE " +
            "AND t.etat between 1 and 99 ")
    long countE();

    @Query("SELECT COUNT(t) " +
            "FROM Tache t " +
            "WHERE t.datef >= CURRENT_DATE " +
            "AND t.etat=0 ")
    long countN();

    @Query("SELECT COUNT(t) " +
            "FROM Tache t " +
            "WHERE t.datef < CURRENT_DATE "+
            "AND t.etat!=100")
    long countR();



   /* ---------------------- TacheRepository User ---------------------- */

    @Query("SELECT t , COUNT(t.id) " +
            "FROM Tache t " +
            "JOIN t.users u " +
            "WHERE (u.id = :userId) " +
            "AND ((t.datef >= CURRENT_DATE) or (t.etat=100)) " +
            "AND (LOWER(t.title) LIKE %:search% " +
            "OR (LOWER(t.description)) LIKE %:search% " +
            "OR TO_CHAR(t.dated, 'YYYY-MM-DD') LIKE %:search% " +
            "OR TO_CHAR(t.datef, 'YYYY-MM-DD') LIKE %:search%) " +
            "GROUP BY t.id")
    Page<Object[]> selectTachesByUserId(@Param("userId") Integer userId, @Param("search") String search, Pageable pageable);

    @Query("SELECT t , COUNT(t.id) " +
            "FROM Tache t " +
            "JOIN t.users u " +
            "WHERE (u.id = :userId) " +
            "GROUP BY t.id")
    List<Tache> selectAllTachesByUserId(@Param("userId") Integer userId);
    @Query("SELECT t , COUNT(t.id) " +
            "FROM Tache t " +
            "JOIN t.users u " +
            "WHERE (u.id = :userId) " +
            "AND t.etat = 100 "+
            "AND t.datef>=t.datefu " +
            "AND (LOWER(t.title) LIKE %:search% " +
            "OR (LOWER(t.description)) LIKE %:search% " +
            "OR TO_CHAR(t.dated, 'YYYY-MM-DD') LIKE %:search% " +
            "OR TO_CHAR(t.datef, 'YYYY-MM-DD') LIKE %:search%) " +
            "GROUP BY t.id")
    Page<Object[]> selectTachesTermineByUserId(@Param("userId") Integer userId, @Param("search") String search, Pageable pageable);

    @Query("SELECT t , COUNT(t.id) " +
            "FROM Tache t " +
            "JOIN t.users u " +
            "WHERE (u.id = :userId) " +
            "AND t.etat between 1 and 99 "+
            "AND t.datef >= CURRENT_DATE " +
            "AND (LOWER(t.title) LIKE %:search% " +
            "OR (LOWER(t.description)) LIKE %:search% " +
            "OR TO_CHAR(t.dated, 'YYYY-MM-DD') LIKE %:search% " +
            "OR TO_CHAR(t.datef, 'YYYY-MM-DD') LIKE %:search%) " +
            "GROUP BY t.id")
    Page<Object[]> selectTachesEnCoursByUserId(@Param("userId") Integer userId, @Param("search") String search, Pageable pageable);

    @Query("SELECT t , COUNT(t.id) " +
            "FROM Tache t " +
            "JOIN t.users u " +
            "WHERE (u.id = :userId) " +
            "AND t.etat = 0 "+
            "AND t.datef >= CURRENT_DATE " +
            "AND (LOWER(t.title) LIKE %:search% " +
            "OR (LOWER(t.description)) LIKE %:search% " +
            "OR TO_CHAR(t.dated, 'YYYY-MM-DD') LIKE %:search% " +
            "OR TO_CHAR(t.datef, 'YYYY-MM-DD') LIKE %:search%) " +
            "GROUP BY t.id")
    Page<Object[]> selectTachesNonCommenceByUserId(@Param("userId") Integer userId, @Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(t) " +
            "FROM Tache t " +
            "WHERE t.etat = 100 "+
            "AND t.users.id = :userId")
    long countTermine(@Param("userId") Integer userId);

    @Query("SELECT COUNT(t) " +
            "FROM Tache t " +
            "WHERE t.datef >= CURRENT_DATE " +
            "AND t.users.id = :userId "+
            "AND t.etat between 1 and 99 ")
    long countENC(@Param("userId") Integer userId);

    @Query("SELECT COUNT(t) " +
            "FROM Tache t " +
            "WHERE t.datef >= CURRENT_DATE " +
            "AND t.users.id = :userId "+
            "AND t.etat=0 ")
    long countNonC(@Param("userId") Integer userId);

    @Query("Select COUNT(DISTINCT p.id) "+
            "FROM Tache t , Project p "+
            "WHERE t.project.id = p.id "+
            "AND t.users.id = :userId")
    long countProject(@Param("userId") Integer userId);



}
