package com.shoppingmall.domain.items.service;

import com.shoppingmall.domain.items.Item;
import com.shoppingmall.domain.items.Outer;
import com.shoppingmall.domain.items.Pants;
import com.shoppingmall.domain.items.Upper;
import com.shoppingmall.domain.items.forms.OuterRegisterForm;
import com.shoppingmall.domain.items.forms.PantsRegisterForm;
import com.shoppingmall.domain.items.forms.UpperRegisterForm;
import com.shoppingmall.domain.items.repository.ItemRepository;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.members.MemberRepository;
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
     * 상품 중 상의 저장 메서드
     * @param upperForm
     * @param salesman
     * @return
     */
    public Long saveUpper(UpperRegisterForm upperForm, Member salesman) {
        Upper upper = Upper.createUpper(upperForm.getName(), upperForm.getPrice(), upperForm.getStockQuantity(), salesman,
                upperForm.getArmLength(), upperForm.getTotalLength(), upperForm.getShoulderWidth());
        itemRepository.save(upper);
        return upper.getId();
    }

    /**
     * 상품 중 바지 저장 메서드
     * @param pantsForm
     * @param salesman
     * @return
     */
    public Long savePants(PantsRegisterForm pantsForm, Member salesman) {
        Pants pants = Pants.createPants(pantsForm.getName(), pantsForm.getPrice(), pantsForm.getStockQuantity(), salesman,
                pantsForm.getTotalLength(), pantsForm.getWaist(), pantsForm.getThighWidth());
        itemRepository.save(pants);
        return pants.getId();
    }

    /**
     * 상품 중 외투 저장 메서드
     * @param outerForm
     * @param salesman
     * @return
     */
    public Long saveOuter(OuterRegisterForm outerForm, Member salesman) {
        Outer outer = Outer.createOuter(outerForm.getName(), outerForm.getPrice(), outerForm.getStockQuantity(), salesman,
                outerForm.getTotalLength(), outerForm.getWeight(), outerForm.getArmLength());
        itemRepository.save(outer);
        return outer.getId();
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

}
