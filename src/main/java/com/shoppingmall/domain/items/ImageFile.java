package com.shoppingmall.domain.items;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageFile {

    @Id @GeneratedValue
    @Column(name = "image_file_id")
    private Long id;

    private String originalFileName;
    private String storeFileName;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public ImageFile(String originalFileName, String storeFileName, Item item) {
        this.originalFileName = originalFileName;
        this.storeFileName = storeFileName;
        this.item = item;
    }
}
