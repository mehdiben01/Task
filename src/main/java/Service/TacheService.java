package Service;

import Model.Tache;
import Repository.TacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TacheService {

    @Autowired
    private TacheRepository tacheRepository;

    public Tache save(Tache tache){
        return  tacheRepository.save(tache);
    }

    public List<Tache> getAllTask(){
        return tacheRepository.findAllByIsDeleted("0");
    }

    public long countTask(){
        return tacheRepository.count();
    }

    public Tache getTacheById(Integer id) {
        return tacheRepository.getTacheById(id)
                .orElseThrow(() -> new NoSuchElementException("Tache not found with ID: " + id));
    }
}
