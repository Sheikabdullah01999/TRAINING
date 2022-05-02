package com.grootan.assetManagement.Repository;

import com.grootan.assetManagement.Model.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryDao extends JpaRepository<History,Integer> {
}
