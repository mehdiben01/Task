package Service;

import Model.Client;
import Model.Utilisateur;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
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

    public boolean addImage(Object entity, MultipartFile imageFile) {

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
    public String getPrivateImageURL(String bucketName, String objectName) {
        try {
            // Générez une URL pré-signée avec une expiration
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .method(Method.GET)
                            .build()
            );
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateImage(Object entity, MultipartFile newImageFile, String objectName) {
        try {
            long maxSize = 2 * 1024 * 1024; // 2 Mo en octets
            if (newImageFile.getSize() > maxSize) {
                return false; // Taille de la nouvelle image dépassée
            }
            // Enregistrez le contenu de la nouvelle image dans MinIO en remplaçant l'objet existant
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("taskmanager") // Remplacez par le nom de votre bucket MinIO
                            .object(objectName) // Utilisez le même chemin (nom) que l'image existante
                            .stream(newImageFile.getInputStream(), newImageFile.getSize(), -1)
                            .build()
            );
            // Mettez à jour le chemin de l'image dans l'objet (s'il est nécessaire de le faire)
            if (entity instanceof Utilisateur) {
                Utilisateur utilisateur = (Utilisateur) entity;
                utilisateur.setCheminImage(objectName);
            } else if (entity instanceof Client) {
                Client client = (Client) entity;
                client.setCheminImage(objectName);
            }
            return true; // Mise à jour réussie
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | ServerException | InsufficientDataException | ErrorResponseException | XmlParserException | InternalException | InvalidResponseException e) {
            e.printStackTrace();
            return false; // Erreur lors de l'enregistrement de l'image dans MinIO
        }
    }

}
