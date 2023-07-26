package Repository;

import Model.Project;
import Model.Tache;
import Model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface TacheRepository extends JpaRepository<Tache, Integer> {

    boolean existsByTitleAndProjectAndUsers(String title, Project project, Utilisateur users );

    boolean existsByTitleAndProjectAndUsersAndIdNot(String title, Project project, Utilisateur users, Integer id );

    boolean existsByTitleAndDescriptionAndDatedAndDatefAndUsersAndProjectAndEtat(String title, String description, String dated, String datef,  Utilisateur users, Project project, Integer etat);
    List<Tache> findAllByIsDeleted(String isDeleted);

    long count();

    Optional<Tache> getTacheById(Integer id);

}
