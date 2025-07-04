package com.example.spring_practice.service;

import com.example.spring_practice.model.entities.AdherentEntity;
import com.example.spring_practice.model.entities.EmpruntEntity;
import com.example.spring_practice.model.entities.StatutEmpruntEntity;
import com.example.spring_practice.model.entities.MvtEmpruntEntity;
import com.example.spring_practice.repository.EmpruntRepository;
import com.example.spring_practice.repository.StatutEmpruntRepository;
import com.example.spring_practice.repository.MvtEmpruntRepository;
import com.example.spring_practice.repository.AdherentRepository;
import com.example.spring_practice.repository.PenaliteRepository;
import com.example.spring_practice.model.entities.PenaliteEntity;
import com.example.spring_practice.model.entities.ExemplaireEntity;
import com.example.spring_practice.repository.AbonnementRepository;
import com.example.spring_practice.model.entities.AbonnementEntity;
import com.example.spring_practice.repository.DroitsEmpruntSpecifiquesRepository;
import com.example.spring_practice.model.entities.DroitsEmpruntSpecifiquesEntity;
import com.example.spring_practice.model.entities.LivreEntity;
import com.example.spring_practice.model.entities.ProfilAdherentEntity;
import com.example.spring_practice.repository.ProlongementRepository;
import com.example.spring_practice.model.entities.ProlongementEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
public class EmpruntService {
    @Autowired
    private EmpruntRepository empruntRepository;
    @Autowired
    private StatutEmpruntRepository statutEmpruntRepository;
    @Autowired
    private MvtEmpruntRepository mvtEmpruntRepository;
    @Autowired
    private AdherentRepository adherentRepository;
    @Autowired
    private PenaliteRepository penaliteRepository;
    @Autowired
    private AbonnementRepository abonnementRepository;
    @Autowired
    private DroitsEmpruntSpecifiquesRepository droitsEmpruntSpecifiquesRepository;
    @Autowired
    private ProlongementRepository prolongementRepository;

    public List<EmpruntEntity> findAll() {
        return empruntRepository.findAll();
    }

    public Optional<EmpruntEntity> findById(Long id) {
        return empruntRepository.findById(id);
    }

    public EmpruntEntity save(EmpruntEntity emprunt) {
        // Recharge l'adhérent pour avoir le profil complet
        AdherentEntity adherent = adherentRepository.findById(emprunt.getAdherent().getId())
            .orElseThrow(() -> new IllegalStateException("Adhérent introuvable"));
        // Vérification des droits d'emprunt spécifiques (âge minimum)
        ExemplaireEntity exemplaire = emprunt.getExemplaire();
        if (exemplaire == null) {
            throw new IllegalStateException("Aucun exemplaire sélectionné pour l'emprunt.");
        }
        LivreEntity livre = exemplaire.getLivre();
        if (livre == null) {
            throw new IllegalStateException("Aucun livre associé à l'exemplaire sélectionné.");
        }
        ProfilAdherentEntity profil = adherent.getProfil();
        List<DroitsEmpruntSpecifiquesEntity> droits = droitsEmpruntSpecifiquesRepository.findAll();
        for (DroitsEmpruntSpecifiquesEntity droit : droits) {
            if (droit.getLivre().getId().equals(livre.getId()) && droit.getProfil().getId().equals(profil.getId())) {
                Integer ageMin = droit.getAge();
                if (ageMin != null) {
                    int ageAdherent = Period.between(adherent.getDateNaissance(), emprunt.getDateEmprunt().toLocalDate()).getYears();
                    if (ageAdherent < ageMin) {
                        throw new IllegalStateException("L'âge minimum pour emprunter ce livre est de " + ageMin + " ans pour ce profil.");
                    }
                }
                // Vérification du droit d'emprunt à domicile
                if (!droit.isEmpruntDomicileAutorise()) {
                    throw new IllegalStateException("Ce profil n'est pas autorisé à emprunter ce livre à domicile.");
                }
            }
        }
        // Vérification des pénalités en cours
        List<PenaliteEntity> penalites = penaliteRepository.findByAdherentId(adherent.getId());
        LocalDate dateEmprunt = emprunt.getDateEmprunt().toLocalDate();
        for (PenaliteEntity p : penalites) {
            LocalDate debut = p.getDateDebut();
            LocalDate fin = debut.plusDays(p.getJour() - 1);
            if ((dateEmprunt.isEqual(debut) || dateEmprunt.isAfter(debut)) && dateEmprunt.isBefore(fin.plusDays(1))) {
                throw new IllegalStateException("L'adhérent a une pénalité en cours à cette date et ne peut pas emprunter.");
            }
        }
        int quota = adherent.getProfil().getQuotaEmpruntsSimultanes();
        List<EmpruntEntity> emprunts = empruntRepository.findByAdherentId(adherent.getId());
        long actifs = emprunts.stream()
            .filter(e -> "En cours".equalsIgnoreCase(getLastStatutForEmprunt(e.getId())))
            .count();
        if (actifs >= quota) {
            throw new IllegalStateException("L'adhérent a déjà atteint son quota d'emprunts simultanés.");
        }
        // Vérification de la disponibilité de l'exemplaire
        int quantiteTotale = exemplaire.getQuantite();
        List<EmpruntEntity> empruntsExemplaire = empruntRepository.findByExemplaireId(exemplaire.getId());
        int disponible = quantiteTotale;
        for (EmpruntEntity e : empruntsExemplaire) {
            String statut = getLastStatutForEmprunt(e.getId());
            if ("En cours".equalsIgnoreCase(statut)) {
                LocalDateTime debutExist = e.getDateEmprunt();
                LocalDateTime finExist = e.getDateRetourPrevue();
                LocalDateTime debutNouveau = emprunt.getDateEmprunt();
                LocalDateTime finNouveau = emprunt.getDateRetourPrevue();
                boolean chevauche = !finNouveau.isBefore(debutExist) && !debutNouveau.isAfter(finExist);
                if (chevauche) {
                    throw new IllegalStateException("Un autre emprunt pour cet exemplaire chevauche la période demandée (statut 'En cours').");
                }
            }
            if ("En cours".equalsIgnoreCase(statut)) {
                disponible -= 1;
            } else if ("Rendu".equalsIgnoreCase(statut)) {
                disponible += 1;
            }
        }
        System.out.println("Exemplaires disponibles pour l'exemplaire ID " + exemplaire.getId() + " : " + disponible);
        if (disponible <= 0) {
            throw new IllegalStateException("Aucun exemplaire disponible pour cet emprunt.");
        }
        // Vérification de l'abonnement actif
        List<AbonnementEntity> abonnements = abonnementRepository.findByAdherentId(adherent.getId());
        boolean actif = abonnements.stream().anyMatch(ab ->
            (dateEmprunt.isEqual(ab.getDateDebut()) || dateEmprunt.isAfter(ab.getDateDebut())) &&
            (dateEmprunt.isEqual(ab.getDateFin()) || dateEmprunt.isBefore(ab.getDateFin()))
        );
        if (!actif) {
            throw new IllegalStateException("L'adhérent n'a pas d'abonnement actif à la date de l'emprunt.");
        }
        emprunt.setAdherent(adherent); // pour que l'emprunt ait l'adhérent complet
        EmpruntEntity savedEmprunt = empruntRepository.save(emprunt);
        // Ajout du mouvement d'emprunt avec statut 'En cours'
        StatutEmpruntEntity statutEnCours = statutEmpruntRepository.findByCodeStatut("En cours")
            .orElseThrow(() -> new IllegalStateException("Statut 'En cours' introuvable"));
        MvtEmpruntEntity mvt = new MvtEmpruntEntity();
        mvt.setEmprunt(savedEmprunt);
        mvt.setStatutNouveau(statutEnCours);
        mvt.setDateMouvement(LocalDateTime.now());
        mvtEmpruntRepository.save(mvt);
        return savedEmprunt;
    }

    public void deleteById(Long id) {
        empruntRepository.deleteById(id);
    }

    public void returnEmprunt(Long empruntId, LocalDateTime dateRetour) {
        EmpruntEntity emprunt = empruntRepository.findById(empruntId)
            .orElseThrow(() -> new IllegalArgumentException("Emprunt non trouvé"));
        emprunt.setDateRetourReelle(dateRetour);
        // Vérification de la date prévue avec prolongement
        LocalDateTime datePrevue = emprunt.getDateRetourPrevue();
        ProlongementEntity prolongement = prolongementRepository.findTopByEmpruntIdOrderByDateFinDesc(empruntId);
        if (prolongement != null && prolongement.getDateFin() != null) {
            datePrevue = prolongement.getDateFin();
        }
        StatutEmpruntEntity statut = statutEmpruntRepository.findByCodeStatut("Rendu")
            .orElseThrow(() -> new IllegalStateException("Statut 'Rendu' introuvable"));
        MvtEmpruntEntity mvt = new MvtEmpruntEntity();
        mvt.setEmprunt(emprunt);
        mvt.setStatutNouveau(statut);
        mvt.setDateMouvement(dateRetour);
        mvtEmpruntRepository.save(mvt);
        empruntRepository.save(emprunt);
    }

    public String getLastStatutForEmprunt(Long empruntId) {
        return mvtEmpruntRepository.findTopByEmpruntIdOrderByDateMouvementDesc(empruntId)
            .map(mvt -> mvt.getStatutNouveau().getCodeStatut())
            .orElse("Inconnu");
    }
} 