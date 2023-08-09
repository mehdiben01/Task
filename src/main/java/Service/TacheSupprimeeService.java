package Service;

import Model.Tache;
import Model.TacheSupprimee;
import Repository.TacheSupprimeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TacheSupprimeeService {
    @Autowired
    private  TacheSupprimeeRepository tacheSupprimeeRepository;
    @Autowired
    public TacheSupprimeeService(TacheSupprimeeRepository tacheSupprimeeRepository) {
        this.tacheSupprimeeRepository = tacheSupprimeeRepository;
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

}
