package com.shoppingmall.domain.members;

import com.shoppingmall.domain.commons.BaseDateInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttachedFile extends BaseDateInfo {

    @Id @GeneratedValue
    @Column(name = "attached_file_id")
    private Long id;

    private String originalFileName;
    private String storeFileName;

    public AttachedFile(String originalFileName, String storeFileName) {
        this.originalFileName = originalFileName;
        this.storeFileName = storeFileName;
    }
}
