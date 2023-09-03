package Service;

import Model.Client;
import Model.Utilisateur;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class ImageService {

    @Autowired
    private MinioClient minioClient; // Injectez le client MinIO configuré

    public static final String IMAGE_SIZE_ERROR_MESSAGE = "La taille de l'image ne peut pas dépasser 2 Mo.";
    public static final String IMAGE_SAVE_ERROR_MESSAGE = "Une erreur s'est produite lors de l'enregistrement de l'image.";

    public boolean updateImage(Object entity, MultipartFile imageFile) {

        try {
            long maxSize = 2 * 1024 * 1024; // 2 Mo en octets
            if (imageFile.getSize() > maxSize) {
                return false; // Taille de l'image dépassée
            }

            String nomImage = imageFile.getOriginalFilename();
            String dossierDestination = "images"; // Vous n'avez pas besoin du "/" initial
            String cheminDestination = dossierDestination + "/" + nomImage;

            // Enregistrez le contenu de l'image dans MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("taskmanager") // Remplacez par le nom de votre bucket MinIO
                            .object(cheminDestination)
                            .stream(imageFile.getInputStream(), imageFile.getSize(), -1)
                            .build()
            );

            // Définir le chemin de l'image dans l'objet
            if (entity instanceof Utilisateur) {
                Utilisateur utilisateur = (Utilisateur) entity;
                utilisateur.setCheminImage(cheminDestination);
            } else if (entity instanceof Client) {
                Client client = (Client) entity;
                client.setCheminImage(cheminDestination);
            }

            return true; // Mise à jour réussie
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | ServerException | InsufficientDataException | ErrorResponseException | XmlParserException | InternalException | InvalidResponseException e) {
            e.printStackTrace();
            return false; // Erreur lors de l'enregistrement de l'image dans MinIO
        }
    }
}
