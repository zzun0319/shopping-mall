package com.shoppingmall.domain.items.forms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRegisterForm {

    @NotEmpty(message = "상품명은 필수입니다.")
    private String name;

    @NotNull(message = "값을 채워주세요.")
    @Range(min = 1000, max = 1000000, message = "가격은 1,000원부터 1,000,000원까지 허용됩니다.")
    private Integer price;

    @NotNull(message = "값을 채워주세요.")
    @Range(min = 1, max = 9999, message = "상품 재고는 최소 1개부터 9999개까지 등록 가능합니다.")
    private Integer stockQuantity;

    private Long salesmanId;

    @NotEmpty(message = "상품 종류를 선택해주세요.")
    private String dType;

    @NotNull(message = "값을 채워주세요.")
    private Integer value1;
    @NotNull(message = "값을 채워주세요.")
    private Integer value2;
    @NotNull(message = "값을 채워주세요.")
    private Integer value3;

    private List<MultipartFile> imageFiles;

    public ItemRegisterForm(String name, Integer price, Integer stockQuantity, Long salesmanId, String dType, Integer value1, Integer value2, Integer value3) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.salesmanId = salesmanId;
        this.dType = dType;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
    }

    public ItemRegisterForm(String dType) {
        this.dType = dType;
    }
}
