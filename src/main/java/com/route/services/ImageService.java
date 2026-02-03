package com.route.services;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.route.models.Image;
import com.route.models.Signalement;
import com.route.repositories.ImageRepository;

// Ajout Firebase
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    private static final String COLLECTION_NAME = "signalement";



    public Image save(Image image){
        return imageRepository.save(image);
    }


   public List<Image> findAllImageFromFireBase(Signalement signalement){
    List<Image> images = new ArrayList<>();
    try {
        Firestore db = FirestoreClient.getFirestore();

        // On récupère la sous-collection "images" du signalement
        ApiFuture<QuerySnapshot> future = db.collection("signalement") // collection parent
                .document(signalement.getFirestoreId())                // document spécifique
                .collection("images")                                  // sous-collection images
                .get();

        QuerySnapshot querySnapshot = future.get();
        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
            Image image = new Image();
            image.setLien(doc.getString("lien"));
            image.setIdFirestore(doc.getId()); // id du document dans la subcollection
            // Date d'ajout si elle existe
            if (doc.contains("date_ajout") && doc.getDate("date_ajout") != null) {
                image.setDate(doc.getDate("date_ajout"));
            }
            // Description si elle existe
            if (doc.contains("description") && doc.getString("description") != null) {
                image.setDescription(doc.getString("description"));
            }
            images.add(image);
        }
    } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
    }
    return images;
}

/**
     * Sauvegarde l'image dans la base de données et upload le fichier sur le serveur.
     * @param file le fichier image à uploader
     * @param image l'objet Image à sauvegarder (doit contenir le signalement associé)
     * @param uploadDir le dossier où stocker les images (ex: "/uploads")
     * @return l'objet Image sauvegardé
     */
    public Image saveImageAndUploadToServer(MultipartFile file, Image image, String uploadDir) throws IOException {
        // Génère un nom de fichier unique
        String extension = "";
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }
        String fileName = UUID.randomUUID().toString() + extension;

        // Crée le dossier si besoin
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Sauvegarde le fichier sur le serveur
        File serverFile = new File(dir, fileName);
        file.transferTo(serverFile);

        // Met à jour le lien dans l'objet Image (ex: "/uploads/nomfichier.jpg")
        image.setLien(uploadDir + "/" + fileName);

        // Enregistre dans la base de données
        return imageRepository.save(image);
    }

    /**
     * Télécharge l'image depuis une URL (lien Firestore), sauvegarde sur le serveur et enregistre dans la base de données.
     * @param imageUrl le lien de l'image à télécharger (depuis Firestore)
     * @param image l'objet Image à sauvegarder (doit contenir le signalement associé)
     * @param uploadDir le dossier où stocker les images (ex: "/uploads")
     * @return l'objet Image sauvegardé
     */
    public Image saveImageFromFirestoreUrl(Image image, String uploadDir) throws IOException {
        // Génère un nom de fichier unique
        String extension = "";
        String fileNameFromUrl = image.getLien().substring(image.getLien().lastIndexOf('/') + 1);
        if (fileNameFromUrl.contains(".")) {
            extension = fileNameFromUrl.substring(fileNameFromUrl.lastIndexOf('.'));
        }
        String fileName = UUID.randomUUID().toString() + extension;

        // Crée le dossier si besoin
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Télécharge l'image depuis l'URL
        URL url = new URL(image.getLien());
        try (InputStream in = url.openStream();
             FileOutputStream out = new FileOutputStream(new File(dir, fileName))) {
            byte[] buffer = new byte[4096];
            int n;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
        }

        // Met à jour le lien dans l'objet Image (ex: "/uploads/nomfichier.jpg")
        image.setLien(uploadDir + "/" + fileName);

        // Enregistre dans la base de données
        return imageRepository.save(image);
    }
}
