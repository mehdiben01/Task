package Service;

import Model.Tache;
import Model.TacheSupprimee;
import Repository.TacheRepository;
import Repository.TacheSupprimeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class TacheSupprimeeService {

    private final  TacheSupprimeeRepository tacheSupprimeeRepository;

    private final  TacheService tacheService;
    private final TacheRepository tacheRepository;
    @Autowired
    public TacheSupprimeeService(TacheSupprimeeRepository tacheSupprimeeRepository,TacheService tacheService, TacheRepository tacheRepository) {
        this.tacheSupprimeeRepository = tacheSupprimeeRepository;

        this.tacheService = tacheService;
        this.tacheRepository = tacheRepository;
    }

    public void transferTacheSupprimee(Tache tache){
        TacheSupprimee tacheSupprimee = new TacheSupprimee();
        tacheSupprimee.setTitle(tache.getTitle());
        tacheSupprimee.setDescription(tache.getDescription());
        tacheSupprimee.setDated(tache.getDated());
        tacheSupprimee.setDatef(tache.getDatef());
        tacheSupprimee.setEtat(tache.getEtat());
        tacheSupprimee.setDatedu(tache.getDatedu());
        tacheSupprimee.setDatefu(tache.getDatefu());
        tacheSupprimee.setProject(tache.getProject());
        tacheSupprimee.setUsers(tache.getUsers());
        tacheSupprimeeRepository.save(tacheSupprimee);
    }

    public Page<Object[]> getAllTacheSupprimeeList(String search, Pageable pageable) {
        return tacheSupprimeeRepository.findAllTacheSupp(search,pageable);
    }

    public TacheSupprimee getTachesuppById(Integer id) {
        return tacheSupprimeeRepository.getTacheSupprimeeById(id)
                .orElseThrow(() -> new NoSuchElementException("Tache supprime not found with ID: " + id));
    }

    @Transactional
    public void deleteTacheSuppById(Integer id) {
        tacheSupprimeeRepository.deleteTacheSupprimeeById(id);
    }


}
