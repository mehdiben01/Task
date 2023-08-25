package Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String dated;

    @Column(nullable = false)
    private String datef;
    @Column(nullable = false)
    private int etat;

    private String datedu="3000";

    private String isDeleted="0";

    private String datefu="0";
    @ManyToOne
    private Project project;

    @ManyToOne
    private Utilisateur users;





}
