package Service;

import Model.Tache;
import Model.TacheSupprimee;
import Repository.TacheRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TacheService {

    private final TacheRepository tacheRepository;

    public TacheService(TacheRepository tacheRepository) {
        this.tacheRepository = tacheRepository;
    }

    public Tache save(Tache tache){
        return  tacheRepository.save(tache);
    }

    public Page<Object[]> getAllTaskByUser(Integer userId,String search, Pageable pageable){
        return tacheRepository.selectTachesByUserId(userId,search, pageable);
    }

    public List<Tache> selectAllTachesByUserId(Integer userId){
        return tacheRepository.selectAllTachesByUserId(userId);
    }
    public Page<Object[]> getAllTaskTermineByUser(Integer userId, String search, Pageable pageable){
        return tacheRepository.selectTachesTermineByUserId(userId,search, pageable);
    }
    public Page<Object[]> getAllTaskEnCoursByUser(Integer userId, String search, Pageable pageable){
        return tacheRepository.selectTachesEnCoursByUserId(userId,search, pageable);
    }

    public Page<Object[]> getAllTaskNonCommenceByUser(Integer userId, String search, Pageable pageable){
        return tacheRepository.selectTachesNonCommenceByUserId(userId,search, pageable);
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

    public List<Tache> getTacheRetDebut(){
        return tacheRepository.selectTacheRetDebut();
    }


    public long countTermine(Integer userId){
        return tacheRepository.countTermine(userId);
    }

    public long countENC(Integer userId){
        return tacheRepository.countENC(userId);
    }

    public long countNonc(Integer userId){
        return tacheRepository.countNonC(userId);
    }

    public long countProject(Integer userId){
        return tacheRepository.countProject(userId);
    }
    public long countT(){
        return tacheRepository.countT();
    }
    public long countE(){
        return tacheRepository.countE();
    }
    public long countN(){
        return tacheRepository.countN();
    }
    public long countR(){
        return tacheRepository.countR();
    }

    @Transactional
    public void deleteTacheById(Integer id) {
        tacheRepository.deleteTacheById(id);
    }

    public Tache getTacheById(Integer id) {
        return tacheRepository.getTacheById(id)
                .orElseThrow(() -> new NoSuchElementException("Tache not found with ID: " + id));
    }

    public void transferTache(TacheSupprimee tacheSupprimee){
        Tache tache = new Tache();
        tache.setTitle(tacheSupprimee.getTitle());
        tache.setDescription(tacheSupprimee.getDescription());
        tache.setDated(tacheSupprimee.getDated());
        tache.setDatef(tacheSupprimee.getDatef());
        tache.setEtat(tacheSupprimee.getEtat());
        tache.setDatedu(tacheSupprimee.getDatedu());
        tache.setDatefu(tacheSupprimee.getDatefu());
        tache.setProject(tacheSupprimee.getProject());
        tache.setUsers(tacheSupprimee.getUsers());
        tacheRepository.save(tache);
    }
}
