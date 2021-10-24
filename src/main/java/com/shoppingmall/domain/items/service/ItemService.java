package com.shoppingmall.domain.items.service;

import com.shoppingmall.domain.items.Item;
import com.shoppingmall.domain.items.Outer;
import com.shoppingmall.domain.items.Pants;
import com.shoppingmall.domain.items.Upper;
import com.shoppingmall.domain.items.forms.ItemRegisterForm;
import com.shoppingmall.domain.items.forms.ItemUpdateForm;
import com.shoppingmall.domain.items.repository.ItemRepository;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.members.repository.MemberRepository;
import com.shoppingmall.exceptions.NoSuchItemException;
import com.shoppingmall.exceptions.NotHaveRightToChangeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    /**
     * 상품 저장 메서드
     * @param form
     * @param salesman
     * @return
     */
    public Item saveItem(ItemRegisterForm form, Member salesman) {
        switch (form.getDType()){
            case "U":
                Upper upper = Upper.createUpper(form.getName(), form.getPrice(), form.getStockQuantity(), salesman,
                        form.getValue1(), form.getValue2(), form.getValue3());
                return itemRepository.save(upper);
            case "P":
                Pants pants = Pants.createPants(form.getName(), form.getPrice(), form.getStockQuantity(), salesman,
                        form.getValue1(), form.getValue2(), form.getValue3());

                return itemRepository.save(pants);
            case "O":
                Outer outer = Outer.createOuter(form.getName(), form.getPrice(), form.getStockQuantity(), salesman,
                        form.getValue1(), form.getValue2(), form.getValue3());
                return itemRepository.save(outer);
        }
        return null;
    }

    /**
     * 수량 수정 메서드 (파라미터만큼 증가)
     * @param salesmanId 상품 등록한 회원의 id
     * @param itemId 어떤 상품인지
     * @param howManyAdd 증가시킬 수량
     */
    public void addQuantity(Long salesmanId, Long itemId, int howManyAdd) {
        Item item = ItemCheck(itemId);
        RightToChangeCheck(salesmanId, item);
        item.addStockQuantity(howManyAdd);
    }

    /**
     * 수량 수정 메서드 (파라미터 만큼 감소)
     * @param salesmanId 상품 등록한 회원의 id
     * @param itemId 어떤 상품인지
     * @param howManyReduce 감소시킬 수량
     */
    public void reduceQuantity(Long salesmanId, Long itemId, int howManyReduce) {
        Item item = ItemCheck(itemId);
        RightToChangeCheck(salesmanId, item);
        item.reduceStockQuantity(howManyReduce);
    }

    /**
     * 등록된 상품인지 조회하여 Item을 리턴해준다.
     * @param itemId
     * @return
     */
    private Item ItemCheck(Long itemId) {
        Optional<Item> oi = itemRepository.findItemAndSalesmanById(itemId);
        return oi.orElseThrow(() -> new NoSuchItemException("존재하지 않는 상품"));
    }

    /**
     * 변경 권한이 있는지 체크. 상품을 등록한 사용자와 id가 일치해야한다.
     * @param salesmanId
     * @param item
     */
    private void RightToChangeCheck(Long salesmanId, Item item) {
        if (item.getSalesman().getId() != salesmanId) {
            throw new NotHaveRightToChangeException("변경 권한이 없습니다");
        }
    }

    /**
     * 상품 수량, 가격 변경 메서드
     * @param itemId
     * @param form
     */
    public void updateItem(Long itemId, ItemUpdateForm form) {

        Optional<Item> oi = itemRepository.findById(itemId);
        Item findItem = oi.orElseThrow(() -> new NoSuchItemException("존재하지 않는 아이템"));

        findItem.changePrice(form.getPrice());
        findItem.changeQuantity(form.getQuantity());
    }
}
