package com.shoppingmall.domain.items.repository;

import com.shoppingmall.domain.items.ImageFile;
import com.shoppingmall.domain.items.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {

    List<ImageFile> findImageFilesByItem(Item item);
}
