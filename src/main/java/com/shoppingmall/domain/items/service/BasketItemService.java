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
     * 모두 상태 업데이트
     */
    public void allUpdate() {
        List<BasketItem> basketItems = basketItemRepository.findAllWithItems();
        for (BasketItem basketItem : basketItems) {
            basketItem.statusUpdate();
        }
    }
}
