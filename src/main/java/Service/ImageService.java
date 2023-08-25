package Service;

import Model.Client;
import Model.Utilisateur;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class ImageService {

    public static final String IMAGE_SIZE_ERROR_MESSAGE = "La taille de l'image ne peut pas dépasser 2 Mo.";
    public static final String IMAGE_SAVE_ERROR_MESSAGE = "Une erreur s'est produite lors de l'enregistrement de l'image.";



    public boolean updateImage(Object entity, MultipartFile imageFile) {


        try {
            long maxSize = 2 * 1024 * 1024; // 2 Mo en octets
            if (imageFile.getSize() > maxSize) {
                return false; // Taille de l'image dépassée
            }

            String nomImage = imageFile.getOriginalFilename();
            String dossierDestination = "/images/";
            String cheminDestination = System.getProperty("user.dir") + "/src/main/resources/static" + dossierDestination + nomImage;

            // Créer les répertoires si nécessaire
            File dossier = new File(dossierDestination);
            dossier.mkdirs();

            // Créer le fichier de destination
            File fichierDestination = new File(cheminDestination);

            // Enregistrer le contenu de l'image dans le fichier de destination
            FileOutputStream fos = new FileOutputStream(fichierDestination);
            fos.write(imageFile.getBytes());
            fos.close();

            // Définir le chemin de l'image dans l'objet
            if (entity instanceof Utilisateur) {
                Utilisateur utilisateur = (Utilisateur) entity;
                utilisateur.setCheminImage(dossierDestination + nomImage);
            } else if (entity instanceof Client) {
                Client client = (Client) entity;
                client.setCheminImage(dossierDestination + nomImage);
            }

            return true; // Mise à jour réussie
        } catch (IOException e) {
            e.printStackTrace();
            return false; // Erreur lors de l'enregistrement du fichier
        }
    }

}
