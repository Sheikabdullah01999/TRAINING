package com.grootan.assetManagement.repository;

import com.grootan.assetManagement.model.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryDao extends JpaRepository<History,Integer> {
}
