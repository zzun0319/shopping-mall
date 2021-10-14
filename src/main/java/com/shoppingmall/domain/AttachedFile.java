package com.shoppingmall.domain;

import com.shoppingmall.domain.commons.BaseDateInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttachedFile extends BaseDateInfo {

    @Id @GeneratedValue
    @Column(name = "attached_file_id")
    private Long id;

    private String uploadFileName;
    private String saveFileName;

}
