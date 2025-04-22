package com.app.stock.repository;

import com.app.stock.model.FolioItem;
import com.app.stock.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolioItemRepository extends JpaRepository<FolioItem, Long> {
    List<FolioItem> findByUser(User user);
    Optional<FolioItem> findByUserAndTicker(User user, String ticker);
}
