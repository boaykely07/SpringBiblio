package com.example.spring_practice.service;

import com.example.spring_practice.model.entities.ProlongementEntity;
import com.example.spring_practice.repository.ProlongementRepository;
import com.example.spring_practice.repository.AbonnementRepository;
import com.example.spring_practice.model.entities.AbonnementEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.spring_practice.model.entities.EmpruntEntity;
import com.example.spring_practice.repository.EmpruntRepository;
import com.example.spring_practice.service.EmpruntService;

import java.util.List;
import java.util.Optional;

@Service
public class ProlongementService {
    @Autowired
    private ProlongementRepository prolongementRepository;

    @Autowired
    private AbonnementRepository abonnementRepository;

    @Autowired
    private EmpruntRepository empruntRepository;

    @Autowired
    private EmpruntService empruntService;

    public List<ProlongementEntity> findAll() {
        return prolongementRepository.findAll();
    }

    public Optional<ProlongementEntity> findById(Long id) {
        return prolongementRepository.findById(id);
    }

    public ProlongementEntity save(ProlongementEntity prolongement) {
        // Vérification de l'abonnement actif à la nouvelle date de fin
        if (prolongement.getEmprunt() != null && prolongement.getEmprunt().getAdherent() != null && prolongement.getDateFin() != null) {
            Long adherentId = prolongement.getEmprunt().getAdherent().getId();
            java.time.LocalDate dateFin = prolongement.getDateFin().toLocalDate();
            java.util.List<AbonnementEntity> abonnements = abonnementRepository.findByAdherentId(adherentId);
            boolean actif = abonnements.stream().anyMatch(ab ->
                (dateFin.isEqual(ab.getDateDebut()) || dateFin.isAfter(ab.getDateDebut())) &&
                (dateFin.isEqual(ab.getDateFin()) || dateFin.isBefore(ab.getDateFin()))
            );
            if (!actif) {
                throw new IllegalStateException("L'adhérent n'a pas d'abonnement actif à la nouvelle date de fin du prolongement.");
            }
        }
        return prolongementRepository.save(prolongement);
    }

    public void deleteById(Long id) {
        prolongementRepository.deleteById(id);
    }

    public List<EmpruntEntity> getEmpruntsProlongeablesPourAdherent(com.example.spring_practice.model.entities.AdherentEntity adherent) {
        if (adherent == null) {
            System.out.println("[DEBUG] Adhérent null dans getEmpruntsProlongeablesPourAdherent");
            return List.of();
        }
        List<EmpruntEntity> emprunts = empruntRepository.findByAdherentId(adherent.getId());
        System.out.println("[DEBUG] Emprunts trouvés pour l'adhérent " + adherent.getNom() + " (id=" + adherent.getId() + ") : " + emprunts.size());
        for (EmpruntEntity e : emprunts) {
            String statut = empruntService.getLastStatutForEmprunt(e.getId());
            System.out.println("[DEBUG] Emprunt #" + e.getId() + " statut: " + statut);
        }
        return emprunts.stream()
            .filter(e -> {
                String statut = empruntService.getLastStatutForEmprunt(e.getId());
                boolean enCours = "En cours".equalsIgnoreCase(statut);
                if (!enCours) {
                    System.out.println("[DEBUG] Emprunt #" + e.getId() + " ignoré car statut: " + statut);
                }
                return enCours;
            })
            .filter(e -> {
                List<ProlongementEntity> prolongements = findAll().stream()
                    .filter(p -> p.getEmprunt() != null && p.getEmprunt().getId().equals(e.getId()))
                    .toList();
                if (prolongements.isEmpty()) {
                    System.out.println("[DEBUG] Emprunt #" + e.getId() + " prolongeable (aucun prolongement)");
                    return true;
                }
                java.time.LocalDateTime maxProlong = prolongements.stream().map(ProlongementEntity::getDateFin).max(java.time.LocalDateTime::compareTo).orElse(null);
                boolean prolongeable = maxProlong == null || maxProlong.isBefore(e.getDateRetourPrevue());
                System.out.println("[DEBUG] Emprunt #" + e.getId() + " maxProlong=" + maxProlong + ", dateRetourPrevue=" + e.getDateRetourPrevue() + ", prolongeable=" + prolongeable);
                return prolongeable;
            })
            .toList();
    }
} 