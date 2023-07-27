package Repository;

import Model.Client;
import Model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Integer> {

    boolean existsByTitleOrDescription(String title, String description);

    boolean existsByTitleAndDescriptionAndDatedAndDatefAndClients(String title, String description, String dated, String datef, Client clients);

    List<Project> findAllByIsDeleted(String isDeleted);

    long countByIsDeleted(String isDeleted);


    @Query("SELECT p, AVG(t.etat), COUNT(t.id) FROM Project p LEFT JOIN p.taches t WHERE p.isDeleted = '0' GROUP BY p")
    Page<Object[]> selectExistingProjectsAndAverageEtat(Pageable pageable);


    @Query("SELECT p.dated, p.datef FROM Project p WHERE p.id = :projectId")
    List<Object[]> findProjectDatesById(@Param("projectId") int projectId);

    Optional<Project> getProjectById(Integer id);

    boolean existsByTitleAndDescriptionAndClientsNot(String title, String description , Client clients);
    boolean existsByTitleAndClients(String title , Client clients);

    boolean existsByTitleAndClientsNot(String title , Client clients);
}
