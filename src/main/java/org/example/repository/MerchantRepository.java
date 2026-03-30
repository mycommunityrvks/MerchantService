package org.example.repository;

import org.example.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    boolean existsByPrimaryPhone(String primaryPhone);

    Optional<Merchant> findByPrimaryPhone(String primaryPhone);
}
