package com.groupeisi.springgraphQL.dao;

import com.groupeisi.springgraphQL.entity.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EtudiantRepository extends JpaRepository<Etudiant, Integer> {
    Etudiant findByEmail(String email);
}
