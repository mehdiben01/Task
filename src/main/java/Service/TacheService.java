package Service;

import Model.Tache;
import Repository.TacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class TacheService {

    @Autowired
    private TacheRepository tacheRepository;

    public Tache save(Tache tache){
        return  tacheRepository.save(tache);
    }

    public Page<Object[]> getAllTask(String search, Pageable pageable){
        return tacheRepository.selectAllTaches(search, pageable);
    }
    public Page<Object[]> getTaskAnnuler(String search, Pageable pageable){
        return tacheRepository.selectAllTachesAnnuler(search,pageable);
    }
    public Page<Object[]> getTaskEncours(String search, Pageable pageable){
        return tacheRepository.selectAllTachesEncours(search,pageable);
    }

    public Page<Object[]> getTaskNonC(String search, Pageable pageable){
        return tacheRepository.selectAllTachesNONC(search,pageable);
    }

    public Page<Object[]> getTaskRet(String search, Pageable pageable){
        return tacheRepository.selectAllTachesRet(search,pageable);
    }

    public Page<Object[]> getTaskTermine(String search, Pageable pageable){
        return tacheRepository.selectAllTachesTermine(search,pageable);
    }

    public Page<Object[]> getTaskTermineEnRet(String search, Pageable pageable){
        return tacheRepository.selectAllTachesTermineEnRet(search,pageable);
    }


    public long countPTermine(){
        return tacheRepository.countPTermine();
    }

    public long countENC(){
        return tacheRepository.countENC();
    }

    public long countNonc(){
        return tacheRepository.countNonC();
    }

    @Transactional
    public void deleteProjectById(Integer id) {
        tacheRepository.deleteTacheById(id);
    }




    public Tache getTacheById(Integer id) {
        return tacheRepository.getTacheById(id)
                .orElseThrow(() -> new NoSuchElementException("Tache not found with ID: " + id));
    }
}
