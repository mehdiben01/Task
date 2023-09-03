package Repository;

import Model.Client;
import Model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Integer> {



    boolean existsByTitleOrDescription(String title, String description);

    boolean existsByTitleAndDescriptionAndDatedAndDatefAndClients(String title, String description, Date dated, Date datef, Client clients);

    List<Project> findAllByIsDeleted(String isDeleted);

    long countByIsDeleted(String isDeleted);

    long count();


    @Query("SELECT p, ROUND(AVG(t.etat), 2), COUNT(t.id) FROM Project p LEFT JOIN p.taches t WHERE p.isDeleted = '0' GROUP BY p")
    List<Object[]> selectExistingProjectsAndAverageEtat();

    @Query("SELECT p, ROUND(AVG(t.etat), 2), COUNT(t.id), MAX(t.datefu) FROM Project p LEFT JOIN p.taches t " +
            "WHERE (" +
            "   LOWER(p.title) LIKE %:search% " +
            "   OR LOWER(p.description) LIKE %:search% " +
            "   OR TO_CHAR(p.dated, 'YYYY-MM-DD') LIKE %:search% " + // Convertir dated en texte
            "   OR TO_CHAR(p.datef, 'YYYY-MM-DD') LIKE %:search%" +
            ") " +
            "AND p.isDeleted = '0' " +
            "AND p.id IN (" +
            "   SELECT DISTINCT p2.id FROM Project p2 LEFT JOIN p2.taches t2 " +
            ") " +
            "GROUP BY p.id")
    Page<Object[]> selectExistingProjectsAndAverageEtatByTitle(@Param("search") String search, Pageable pageable);

    @Query("SELECT p.dated, p.datef FROM Project p WHERE p.id = :projectId")
    List<Object[]> findProjectDatesById(@Param("projectId") int projectId);

    Optional<Project> getProjectById(Integer id);

    boolean existsByTitleAndDescriptionAndClientsNot(String title, String description , Client clients);
    boolean existsByTitleAndIdNot(String title, Integer id);

    boolean existsByTitleAndClientsNot(String title , Client clients);

    @Query("SELECT p, ROUND(AVG(t.etat), 2), COUNT(t.id) FROM Project p LEFT JOIN p.taches t WHERE p.id = :id  GROUP BY p")
    List<Object[]> selectDetailProjct(@Param("id") Integer id);

    @Query("SELECT p, ROUND(AVG(t.etat), 2) AS averageState, COUNT(t.id), MAX(t.datefu) " +
            "FROM Project p " +
            "LEFT JOIN p.taches t " +
            "WHERE p.isDeleted = '0' " +
            "AND (LOWER(p.title) LIKE %:search% " +
            "OR LOWER(p.description) LIKE %:search% " +
            "OR LOWER(p.dated) LIKE %:search% " +
            "OR LOWER(p.datef) LIKE %:search%) " +
            "AND p.id IN (" +
            "   SELECT DISTINCT t.project.id " +
            "   FROM Tache t " +
            "   GROUP BY t.project.id " +
            "   HAVING AVG(t.etat) = 100 " +
            ") " +
            "GROUP BY p ")
    Page<Object[]> selectExistingProjectsTermine(@Param("search") String search, Pageable pageable);

    @Query("SELECT p, ROUND(AVG(t.etat), 2) AS averageState, COUNT(t.id), MAX(t.datefu) " +
            "FROM Project p LEFT JOIN p.taches t " +
            "WHERE p.isDeleted = '0'  " +
            "AND TO_DATE(p.datef, 'YYYY-MM-DD') >= CURRENT_DATE " +
            "AND (LOWER(p.title) LIKE %:search% " +
            "OR LOWER(p.description) LIKE %:search% " +
            "OR TO_CHAR(p.dated, 'YYYY-MM-DD') LIKE %:search% " +
            "OR TO_CHAR(p.datef, 'YYYY-MM-DD') LIKE %:search%) " +
            "AND p.id IN (" +
            "   SELECT DISTINCT t.project.id " +
            "   FROM Tache t " +
            "   GROUP BY t.project.id " +
            "   HAVING AVG(t.etat) BETWEEN 1 and 99" +
            ") " +
            "GROUP BY p ")
    Page<Object[]> selectExistingProjectsEnCours(@Param("search") String search, Pageable pageable);


    @Query("SELECT p , count(p.id) " +
            "FROM Project p " +
            "WHERE p.isDeleted = '1'  " +
            "AND (LOWER(p.title) LIKE %:search% " +
            "OR LOWER(p.description) LIKE %:search% " +
            "OR TO_CHAR(p.dated, 'YYYY-MM-DD') LIKE %:search% " +
            "OR TO_CHAR(p.datef, 'YYYY-MM-DD') LIKE %:search%) " +
            "GROUP BY p.id ")
    Page<Object[]> selectExistingProjectsAnnule(@Param("search") String search, Pageable pageable);

    @Query("SELECT p , count(p.id) " +
            "FROM Project p " +
            "WHERE p.isDeleted = '0'  " +
            "AND TO_DATE(p.datef, 'YYYY-MM-DD') >= CURRENT_DATE " +
            "AND (LOWER(p.title) LIKE %:search% " +
            "OR LOWER(p.description) LIKE %:search% " +
            "OR TO_CHAR(p.dated, 'YYYY-MM-DD') LIKE %:search% " +
            "OR TO_CHAR(p.datef, 'YYYY-MM-DD') LIKE %:search%) " +
            "AND ( " +
            "    NOT EXISTS (SELECT 1 FROM Tache t WHERE t.project = p) " +
            "    OR p.id IN ( " +
            "        SELECT DISTINCT p2.id FROM Project p2 " +
            "        LEFT JOIN p2.taches t2 " +
            "        GROUP BY p2.id " +
            "        HAVING AVG(t2.etat) = 0 OR AVG(t2.etat) IS NULL " +
            "    ) " +
            ")" +
             "GROUP BY p.id")
    Page<Object[]> selectExistingProjectsNCommence(@Param("search") String search, Pageable pageable);

    @Query("SELECT p , count(p.id) " +
            "FROM Project p " +
            "WHERE p.isDeleted = '0'  " +
            "AND TO_DATE(p.datef, 'YYYY-MM-DD') < CURRENT_DATE " +
            "AND (LOWER(p.title) LIKE %:search% " +
            "OR LOWER(p.description) LIKE %:search% " +
            "OR TO_CHAR(p.dated, 'YYYY-MM-DD') LIKE %:search% " +
            "OR TO_CHAR(p.datef, 'YYYY-MM-DD') LIKE %:search%) " +
            "GROUP BY p.id")
    Page<Object[]> selectExistingProjectsRetarde(@Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(p.id) " +
            "FROM Project p " +
            "WHERE TO_DATE(p.datef, 'YYYY-MM-DD') < CURRENT_DATE " +
            "AND p.isDeleted = '0' ")
    long countPR();








}
