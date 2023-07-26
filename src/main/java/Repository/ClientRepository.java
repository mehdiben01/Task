package Repository;

import Model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Integer> {

    boolean existsByEmailOrTelOrCompany( String email, String tel , String company);

    boolean existsByNomAndPrenomAndEmailAndTelAndCompany(String nom,String prenom, String email, String tel , String company);

    boolean existsByNomAndPrenomAndIdNot(String nom,String prenom, Integer id);

    boolean existsByTelAndIdNot(String tel, Integer id);

    boolean existsByCompanyAndIdNot(String company, Integer id);

    List<Client> findAllByIsDeleted(String isDeleted);

    long countByIsDeleted(String isDeleted);

    Optional<Client> getClientById(Integer id);
}
