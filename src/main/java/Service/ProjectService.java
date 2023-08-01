package Service;

import Model.Project;
import Repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;



    public Project save(Project project){
        return projectRepository.save(project);
    }


    public List<Project> getAllProject(){
        return  projectRepository.findAllByIsDeleted("0");
    }

    public long countProjects(){
        return projectRepository.countByIsDeleted("0");
    }
    public long countAllProjects(){
        return projectRepository.count();
    }

    public long countProjectsDeleted(){
        return projectRepository.countByIsDeleted("1");
    }

    public List<Object[]> getExistingProjectsAndAverageEtat(){
        return projectRepository.selectExistingProjectsAndAverageEtat();
    }
    public Page<Object[]> getExistingProjectsAndAverageEtatByTitle(String searchText, Pageable pageable) {
        return projectRepository.selectExistingProjectsAndAverageEtatByTitle(searchText, pageable);
    }

    public Page<Object[]> getProjectTermine(String search, Pageable pageable){
        return projectRepository.selectExistingProjectsTermine(search, pageable);
    }

    public Page<Object[]> getProjectEnCours(String search, Pageable pageable){
        return projectRepository.selectExistingProjectsEnCours(search,pageable);
    }

    public Page<Object[]> getProjectAnnule(String search, Pageable pageable){
        return projectRepository.selectExistingProjectsAnnule(search,pageable);
    }

    public Page<Object[]> getProjectNcommence(String search, Pageable pageable){
        return projectRepository.selectExistingProjectsNCommence(search, pageable);
    }

    public Page<Object[]> getProjectRetarde(String search, Pageable pageable){
        return projectRepository.selectExistingProjectsRetarde(search,pageable);
    }

    public Project getProjectById(Integer id) {
        return projectRepository.getProjectById(id)
                .orElseThrow(() -> new NoSuchElementException("Tache not found with ID: " + id));
    }


    public List<Object[]> getProjectDetailsById(Integer id) {
        // Appeler la méthode du repository pour récupérer les détails du projet par ID
        return projectRepository.selectDetailProjct(id);
    }

    public long countPR(){
        return projectRepository.countPR();
    }




}
