package com.example.spring_practice.service;

import com.example.spring_practice.model.entities.MembreForm;
import com.example.spring_practice.model.entities.UtilisateurEntity;
import com.example.spring_practice.model.entities.AdherentEntity;
import com.example.spring_practice.model.entities.ProfilAdherentEntity;
import com.example.spring_practice.repository.UtilisateurRepository;
import com.example.spring_practice.repository.ProfilAdherentRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import com.example.spring_practice.repository.AdherentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdherentService {

    private final AdherentRepository adherentRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private ProfilAdherentRepository profilAdherentRepository;

    public AdherentService(AdherentRepository adherentRepository) {
        this.adherentRepository = adherentRepository;
    }

    public List<AdherentEntity> findAll() {
        return adherentRepository.findAll();
    }

    public String inscrireMembre(MembreForm form) {
        if (utilisateurRepository.findByEmail(form.getEmail()).isPresent()) {
            return "Cet email est déjà utilisé.";
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        UtilisateurEntity utilisateur = new UtilisateurEntity();
        utilisateur.setEmail(form.getEmail());
        utilisateur.setMotDePasseHash(encoder.encode(form.getMotDePasse()));
        utilisateur = utilisateurRepository.save(utilisateur);

        AdherentEntity adherent = new AdherentEntity();
        adherent.setUtilisateur(utilisateur);
        adherent.setNom(form.getNom());
        adherent.setPrenom(form.getPrenom());
        adherent.setDateNaissance(form.getDateNaissance());
        // Profil par défaut : premier profil trouvé
        Optional<ProfilAdherentEntity> profil = profilAdherentRepository.findAll().stream().findFirst();
        if (profil.isEmpty()) {
            return "Aucun profil adhérent n'est défini.";
        }
        adherent.setProfil(profil.get());
        adherentRepository.save(adherent);
        return null;
    }
}
