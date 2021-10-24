package com.shoppingmall.domain.items.service;

import com.shoppingmall.domain.items.BasketItem;
import com.shoppingmall.domain.items.repository.BasketItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BasketItemService {

    private final BasketItemRepository basketItemRepository;

    /**
     * 모든 장바구니에 담은 상품들 상태 업데이트
     * 수정필요: 멤버별!
     */
    public void allUpdate() {
        List<BasketItem> basketItems = basketItemRepository.findAllWithItems();
        for (BasketItem basketItem : basketItems) {
            basketItem.statusUpdate();
        }
    }
}
