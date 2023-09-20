package Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor()
@NoArgsConstructor()
public class Utilisateur {


    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nom;
    @Column(nullable = false)
    private String prenom;
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date daten;
    @Column(nullable = false)
    private String tel;
    @Column(nullable = false)
    private String password;
    @Column(unique = true)
    private String username;


    private String isDeleted ="0";

    @Transient
    private MultipartFile image;
    private String cheminImage;
    @Column(nullable = false)
    private String profession;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tache> taches;

    public String getFullName() {
        return this.nom + " " + this.prenom; // Adjust this based on your actual field names in the Utilisateur model
    }

    public String getProfession(){
        return this.profession;
    }

    public String getCheminImage(){
        return this.cheminImage;
    }





}
