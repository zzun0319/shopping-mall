package com.shoppingmall.domain.items.controller;

import com.shoppingmall.domain.items.Item;
import com.shoppingmall.domain.items.Outer;
import com.shoppingmall.domain.items.Pants;
import com.shoppingmall.domain.items.Upper;
import com.shoppingmall.domain.items.dtos.ItemDto;
import com.shoppingmall.domain.items.forms.ItemRegisterForm;
import com.shoppingmall.domain.items.forms.OuterRegisterForm;
import com.shoppingmall.domain.items.forms.PantsRegisterForm;
import com.shoppingmall.domain.items.forms.UpperRegisterForm;
import com.shoppingmall.domain.items.repository.ItemRepository;
import com.shoppingmall.domain.items.service.ItemService;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.members.MemberRepository;
import com.shoppingmall.exceptions.CannotSaleItemException;
import com.shoppingmall.exceptions.NoSuchItemException;
import com.shoppingmall.exceptions.NoSuchMemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;

    /**
     * 상품 목록 보기
     * @param model
     * @param pageable
     * @return
     */
    @GetMapping
    public String items(Model model, @PageableDefault(size = 5, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Item> page = itemRepository.findAll(pageable);
        Page<ItemDto> dtoPage = page.map(ItemDto::new);
        model.addAttribute("page", dtoPage);
        return "item/item-list";
    }

    @GetMapping("/add")
    public String itemRegisterForm(@ModelAttribute("form") ItemRegisterForm form) {
        return "item/item-register";
    }

    @PostMapping("/add")
    public String itemTo(@Validated @ModelAttribute("form") ItemRegisterForm form, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "item/item-register";
        }

        switch (form.getDType()) {
            case "U":
                model.addAttribute("u_form", new UpperRegisterForm());
                return "item/upper-register";
            case "P":
                model.addAttribute("p_form", new PantsRegisterForm());
                return "item/pants-register";
            case "O":
                model.addAttribute("o_form", new OuterRegisterForm());
                return "item/outer-register";
        }
        return null;
    }

    @PostMapping("/add/upper")
    public String saveUpper(@ModelAttribute("form") UpperRegisterForm form) {

        Member salesman = getSalesman(form);
        availableSale(salesman);

        itemService.saveUpper(form, salesman);

        return "redirect: /items";
    }

    @PostMapping("/add/pants")
    public String savePants(@ModelAttribute("form") PantsRegisterForm form) {

        Member salesman = getSalesman(form);
        availableSale(salesman);

        itemService.savePants(form, salesman);

        return "redirect: /items";
    }

    @PostMapping("/add/outer")
    public String saveOuter(@ModelAttribute("form") OuterRegisterForm form) {

        Member salesman = getSalesman(form);
        availableSale(salesman);

        itemService.saveOuter(form, salesman);

        return "redirect: /items";
    }

    /**
     * item의 종류가 무엇인지 파악해서 model에 담아주는 메서드.
     * @param model
     * @param item
     */
    private void itemClassification(Model model, Item item) {
        if(item instanceof Upper){
            Upper upper = (Upper) item;
            model.addAttribute("upper", upper);
        } else if (item instanceof Pants) {
            Pants pants = (Pants) item;
            model.addAttribute("pants", pants);
        } else {
            Outer outer = (Outer) item;
            model.addAttribute("outer", outer);
        }
    }

    // 판매가능한지 체크
    private void availableSale(Member salesman) {
        if(!salesman.getSaleAvailable()){
            throw new CannotSaleItemException("상품 판매 허가가 나지 않았습니다.");
        }
    }

    // form에서 회원 정보를 꺼내 리턴
    private Member getSalesman(ItemRegisterForm form) {
        Optional<Member> om = memberRepository.findById(form.getSalesmanId());
        Member salesman = om.orElseThrow(() -> new NoSuchMemberException("존재하지 않는 회원입니다."));
        return salesman;
    }

}
