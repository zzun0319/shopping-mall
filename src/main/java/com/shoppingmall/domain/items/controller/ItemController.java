package com.shoppingmall.domain.items.controller;

import com.shoppingmall.domain.enums.PaymentOption;
import com.shoppingmall.domain.items.*;
import com.shoppingmall.domain.items.dtos.ItemDto;
import com.shoppingmall.domain.items.forms.ItemRegisterForm;
import com.shoppingmall.domain.items.forms.OuterRegisterForm;
import com.shoppingmall.domain.items.forms.PantsRegisterForm;
import com.shoppingmall.domain.items.forms.UpperRegisterForm;
import com.shoppingmall.domain.items.repository.ImageFileRepository;
import com.shoppingmall.domain.items.repository.ItemRepository;
import com.shoppingmall.domain.items.service.ItemService;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.members.dtos.MemberDto;
import com.shoppingmall.domain.members.repository.MemberRepository;
import com.shoppingmall.domain.orders.forms.OrderForm;
import com.shoppingmall.domain.utils.FileStoreUtil;
import com.shoppingmall.exceptions.CannotSaleItemException;
import com.shoppingmall.exceptions.NoSuchItemException;
import com.shoppingmall.exceptions.NoSuchMemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;

    private final ImageFileRepository imageFileRepository;
    private final FileStoreUtil fileStoreUtil;

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

    /**
     * 상품 상세보기
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/{id}")
    public String itemDetail(@PathVariable("id") Long id, Model model, HttpSession session) {

        // 상품 조회
        Optional<Item> oi = itemRepository.findById(id);
        Item item = oi.orElse(null);
        if (item == null) {
            throw new NoSuchItemException("존재하지 않는 상품입니다.");
        }

        // 상품 분류
        itemClassification(model, item);

        List<ImageFile> imageFiles = imageFileRepository.findImageFilesByItem(item);
        model.addAttribute("imageFiles", imageFiles);

        return "item/item-detail";
    }

    /**
     * 이미지 다운로드
     * @param filename
     * @return
     * @throws MalformedURLException
     */
    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStoreUtil.getImageFullPath(filename));
    }

    /**
     * 상품 등록 폼 화면으로 이동
     * @param form
     * @return
     */
    @GetMapping("/add")
    public String itemRegisterForm(@ModelAttribute("form") ItemRegisterForm form) {
        return "item/item-register";
    }

    /**
     * 상품 등록 첫번째 단계
     * @param form
     * @param bindingResult
     * @param model
     * @return
     */
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

    /**
     * 상의 등록
     * @param form
     * @return
     */
    @PostMapping("/add/upper")
    public String saveUpper(@ModelAttribute("form") UpperRegisterForm form) {

        Member salesman = getSalesman(form);
        availableSale(salesman);

        itemService.saveUpper(form, salesman);

        return "redirect: /items";
    }

    /**
     * 바지 등록
     * @param form
     * @return
     */
    @PostMapping("/add/pants")
    public String savePants(@ModelAttribute("form") PantsRegisterForm form) {

        Member salesman = getSalesman(form);
        availableSale(salesman);

        itemService.savePants(form, salesman);

        return "redirect: /items";
    }

    /**
     * 외투 등록
     * @param form
     * @return
     */
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

    /**
     * 판매가능한지 체크
     * @param salesman
     */
    private void availableSale(Member salesman) {
        if(!salesman.getSaleAvailable()){
            throw new CannotSaleItemException("상품 판매 허가가 나지 않았습니다.");
        }
    }

    /**
     * form에서 회원 정보를 꺼내 리턴
     * @param form
     * @return
     */
    private Member getSalesman(ItemRegisterForm form) {
        Optional<Member> om = memberRepository.findById(form.getSalesmanId());
        Member salesman = om.orElseThrow(() -> new NoSuchMemberException("존재하지 않는 회원입니다."));
        return salesman;
    }

}
