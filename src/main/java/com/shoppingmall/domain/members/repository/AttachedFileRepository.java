package com.shoppingmall.domain.members.repository;

import com.shoppingmall.domain.members.AttachedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachedFileRepository extends JpaRepository<AttachedFile, Long> {
}
