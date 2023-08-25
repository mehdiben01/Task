package Repository;

import Model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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



    @Query("SELECT c , count(c.id) "+
           "FROM Client c "+
            "WHERE c.isDeleted ='0' "+
            "AND (LOWER(c.nom) LIKE %:search% "+
            "OR LOWER(c.prenom) LIKE %:search% "+
            "OR LOWER(c.tel) LIKE %:search%" +
            "OR LOWER(c.email) LIKE %:search%" +
            "OR LOWER(c.departement) LIKE %:search%" +
            "OR LOWER(c.company) LIKE %:search%) " +
            "GROUP BY c.id ")
    Page<Object[]> findAllCLient(@Param("search") String search, Pageable pageable);

    @Query("SELECT c , count(c.id) "+
            "FROM Client c "+
            "WHERE c.isDeleted ='1' "+
            "AND (LOWER(c.nom) LIKE %:search% "+
            "OR LOWER(c.prenom) LIKE %:search% "+
            "OR LOWER(c.tel) LIKE %:search%" +
            "OR LOWER(c.email) LIKE %:search%" +
            "OR LOWER(c.departement) LIKE %:search%" +
            "OR LOWER(c.company) LIKE %:search%) " +
            "GROUP BY c.id ")
    Page<Object[]> findAllCLientSupp(@Param("search") String search, Pageable pageable);

}
