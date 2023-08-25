package Repository;

import Model.TacheSupprimee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TacheSupprimeeRepository  extends JpaRepository<TacheSupprimee ,Integer> {

    @Query("SELECT t " +
            "FROM TacheSupprimee t " +
           "WHERE (LOWER(t.title) LIKE %:search% " +
           "OR LOWER(t.description) LIKE %:search% " +
           "OR LOWER(t.dated) LIKE %:search% " +
           "OR LOWER(t.datef) LIKE %:search% ) ")
    Page<Object[]> findAllTacheSupp(@Param("search") String search, Pageable pageable);

    void deleteTacheSupprimeeById(Integer id);

    Optional<TacheSupprimee> getTacheSupprimeeById(Integer id);
}
