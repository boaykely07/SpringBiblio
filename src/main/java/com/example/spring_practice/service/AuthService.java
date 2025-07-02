package com.example.spring_practice.service;

import com.example.spring_practice.model.entities.*;
import com.example.spring_practice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private AdherentRepository adherentRepository;
    @Autowired
    private BibliothecaireRepository bibliothecaireRepository;
    @Autowired
    private ProfilAdherentRepository profilAdherentRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UtilisateurEntity registerClient(String email, String password, String nom, String prenom,
            LocalDate dateNaissance, Long idProfil) {
        if (utilisateurRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }
        UtilisateurEntity utilisateur = new UtilisateurEntity();
        utilisateur.setEmail(email);
        utilisateur.setMotDePasseHash(passwordEncoder.encode(password));
        utilisateur = utilisateurRepository.save(utilisateur);

        ProfilAdherentEntity profil = profilAdherentRepository.findById(idProfil)
                .orElseThrow(() -> new RuntimeException("Profil adhérent introuvable"));

        AdherentEntity adherent = new AdherentEntity();
        adherent.setUtilisateur(utilisateur);
        adherent.setNom(nom);
        adherent.setPrenom(prenom);
        adherent.setDateNaissance(dateNaissance);
        adherent.setProfil(profil);
        adherentRepository.save(adherent);
        return utilisateur;
    }

    public UtilisateurEntity registerBibliothecaire(String email, String password, String nom, String prenom) {
        if (utilisateurRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }
        UtilisateurEntity utilisateur = new UtilisateurEntity();
        utilisateur.setEmail(email);
        utilisateur.setMotDePasseHash(passwordEncoder.encode(password));
        utilisateur = utilisateurRepository.save(utilisateur);

        BibliothecaireEntity bibliothecaire = new BibliothecaireEntity();
        bibliothecaire.setUtilisateur(utilisateur);
        bibliothecaire.setNom(nom);
        bibliothecaire.setPrenom(prenom);
        bibliothecaireRepository.save(bibliothecaire);
        return utilisateur;
    }

    public UtilisateurEntity login(String email, String password) {
        Optional<UtilisateurEntity> utilisateurOpt = utilisateurRepository.findByEmail(email);
        if (utilisateurOpt.isPresent()) {
            UtilisateurEntity utilisateur = utilisateurOpt.get();
            System.out.println(password);
            if (passwordEncoder.matches(password, utilisateur.getMotDePasseHash())) {
                return utilisateur;
            }
        }
        throw new RuntimeException("Email ou mot de passe incorrect");
    }
}