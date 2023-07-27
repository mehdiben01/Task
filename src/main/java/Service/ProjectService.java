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

    public Page<Object[]> getExistingProjectsAndAverageEtat(Pageable pageable) {
        return projectRepository.selectExistingProjectsAndAverageEtat(pageable);
    }
    public Project getProjectById(Integer id) {
        return projectRepository.getProjectById(id)
                .orElseThrow(() -> new NoSuchElementException("Tache not found with ID: " + id));
    }




}
