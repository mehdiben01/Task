package Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.List;


@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Client  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String nom;
    @Column(nullable = false)
    private String prenom;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String tel;
    @Column(nullable = false)
    private String company;
    @Column(nullable = false)
    private String departement;
    @Column(nullable = false)
    @Transient
    private MultipartFile logo;

    private String cheminImage;

    private String isDeleted = "0";


    @OneToMany(mappedBy = "clients", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Project> projets;


}
