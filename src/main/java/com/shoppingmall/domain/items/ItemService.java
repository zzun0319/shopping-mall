package com.shoppingmall.domain.items;

import com.shoppingmall.domain.items.forms.ItemRegisterForm;
import com.shoppingmall.domain.items.forms.OuterRegisterForm;
import com.shoppingmall.domain.items.forms.PantsRegisterForm;
import com.shoppingmall.domain.items.forms.UpperRegisterForm;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.members.MemberRepository;
import com.shoppingmall.exceptions.CannotSaleItemException;
import com.shoppingmall.exceptions.NoSuchItemException;
import com.shoppingmall.exceptions.NoSuchMemberException;
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
     * 컨트롤러에서 넘겨준 등록폼을 Item 엔티티로 변환하여 Insert
     * @param registerForm
     */
    public void saveItem(ItemRegisterForm registerForm) {

        Optional<Member> om = memberRepository.findById(registerForm.getSalesmanId());
        Member salesman = om.orElseThrow(() -> new NoSuchMemberException("존재하지 않는 회원입니다."));

        if(!salesman.getSaleAvailable()){
            throw new CannotSaleItemException("상품 판매 허가가 나지 않았습니다.");
        }

        switch (registerForm.getDType()){
            case "U":
                saveUpper((UpperRegisterForm) registerForm, salesman);
                break;
            case "P":
                savePants((PantsRegisterForm) registerForm, salesman);
                break;
            case "O":
                saveOuter((OuterRegisterForm) registerForm, salesman);
                break;
        }

    }

    private void saveOuter(OuterRegisterForm registerForm, Member salesman) {
        OuterRegisterForm outerForm = registerForm;
        Item item = Outer.createOuter(outerForm.getName(), outerForm.getPrice(), outerForm.getStockQuantity(), salesman,
                outerForm.getTotalLength(), outerForm.getWeight(), outerForm.getArmLength());
        itemRepository.save(item);
    }

    private void savePants(PantsRegisterForm registerForm, Member salesman) {
        PantsRegisterForm pantsForm = registerForm;
        Item item = Pants.createPants(pantsForm.getName(), pantsForm.getPrice(), pantsForm.getStockQuantity(), salesman,
                pantsForm.getTotalLength(), pantsForm.getWaist(), pantsForm.getThighWidth());
        itemRepository.save(item);
    }

    private void saveUpper(UpperRegisterForm registerForm, Member salesman) {
        UpperRegisterForm upperForm = registerForm;
        Item item = Upper.createUpper(upperForm.getName(), upperForm.getPrice(), upperForm.getStockQuantity(), salesman,
                upperForm.getArmLength(), upperForm.getTotalLength(), upperForm.getShoulderWidth());
        itemRepository.save(item);
    }

    // 수량 수정 메서드 (파라미터만큼 증가)
    private void addQuantity(Long itemId, int howManyAdd) {
        Optional<Item> oi = itemRepository.findById(itemId);
        Item item = oi.orElseThrow(() -> new NoSuchItemException("존재하지 않는 상품"));
        item.addStockQuantity(howManyAdd);
    }

    // 수량 수정 메서드 (파라미터 만큼 감소)
    private void reduceQuantity(Long itemId, int howManyReduce) {
        Optional<Item> oi = itemRepository.findById(itemId);
        Item item = oi.orElseThrow(() -> new NoSuchItemException("존재하지 않는 상품"));
        item.reduceStockQuantity(howManyReduce);
    }

}
