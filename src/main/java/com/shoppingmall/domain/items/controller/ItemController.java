package com.shoppingmall.domain.items.controller;

import com.shoppingmall.domain.items.*;
import com.shoppingmall.domain.items.dtos.ItemDto;
import com.shoppingmall.domain.items.forms.ItemRegisterForm;
import com.shoppingmall.domain.items.forms.ItemUpdateForm;
import com.shoppingmall.domain.items.repository.ImageFileRepository;
import com.shoppingmall.domain.items.repository.ItemRepository;
import com.shoppingmall.domain.items.service.ItemService;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.members.dtos.MemberDto;
import com.shoppingmall.domain.members.repository.MemberRepository;
import com.shoppingmall.domain.utils.FileStoreUtil;
import com.shoppingmall.domain.valuetype.ItemType;
import com.shoppingmall.exceptions.CannotSaleItemException;
import com.shoppingmall.exceptions.NoSuchItemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

@Slf4j
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
        Optional<Item> oi = itemRepository.findItemAndSalesmanById(id);
        Item item = oi.orElse(null);
        if (item == null) {
            throw new NoSuchItemException("존재하지 않는 상품입니다.");
        }

        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");
        if (loginMember != null) {
            log.info("세션있음");
            if (loginMember.getId() == item.getSalesman().getId()) {
                log.info("세션의 멤버와 item을 등록한 멤버가 같음");
                model.addAttribute("samePerson", true);
            }
        }

        // 상품 분류
        itemClassification(model, item);

        // 상품 이미지파일들
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
     * 상품 종류 선택 페이지로 이동
     * @return
     */
    @GetMapping("/type")
    public String itemTypeSelectPage(Model model){

        List<ItemType> itemTypes = new ArrayList<>();
        itemTypes.add(new ItemType("U", "상의"));
        itemTypes.add(new ItemType("P", "바지"));
        itemTypes.add(new ItemType("O", "외투"));
        model.addAttribute("itemTypes", itemTypes);

        return "item/select-type";
    }

    /**
     * 상품 종류를 받아서 상품 등록 폼 페이지로
     * @param itemType
     * @param model
     * @return
     */
    @PostMapping("/type")
    public String itemTypeSelectAndGoToRegisterForm(String itemType, Model model){

        model.addAttribute("form", new ItemRegisterForm(itemType));

        return "item/add-item-form";
    }

    /**
     * 상품 등록 두번째 단계
     * @param form
     * @param bindingResult
     * @return
     */
    @PostMapping
    public String itemRegister(@Validated @ModelAttribute("form") ItemRegisterForm form,
                               BindingResult bindingResult, HttpSession session, RedirectAttributes ra) throws IOException {

        if (bindingResult.hasErrors()) {
            return "item/add-item-form";
        }

        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        if (loginMember != null) {
            Member salesman = memberRepository.findById(loginMember.getId()).get();
            Item savedItem = saveItem(form, salesman);
            List<ImageFile> imageFiles = fileStoreUtil.storeImageFiles(form.getImageFiles(), savedItem);
            imageFiles.stream().forEach(image -> imageFileRepository.save(image));

            ra.addAttribute("itemId", savedItem.getId());
            return "redirect:/items/{itemId}"; // 바꿔 아이템 상세보기로
        }

        return "redirect:/";
    }

    /**
     * 상품 수정 페이지
     * @param itemId
     * @param model
     * @return
     */
    @GetMapping("/edit/{itemId}")
    public String editPage(@PathVariable("itemId") Long itemId, Model model) {

        Optional<Item> oi = itemRepository.findById(itemId);
        Item findItem = oi.orElseThrow(() -> new NoSuchItemException("존재하지 않는 아이템"));

        ItemUpdateForm form = new ItemUpdateForm(findItem.getName(), findItem.getPrice(), findItem.getStockQuantity());
        model.addAttribute("form", form);

        return "item/item-update";
    }

    /**
     * 상품 수정
     * @param itemId
     * @param form
     * @param bindingResult
     * @param ra
     * @return
     */
    @PostMapping("/edit/{itemId}")
    public String editPage(@PathVariable("itemId") Long itemId, @Validated @ModelAttribute("form") ItemUpdateForm form,
                           BindingResult bindingResult, RedirectAttributes ra) {

        itemService.updateItem(itemId, form);

        ra.addAttribute("id", itemId);
        return "redirect:/items/{id}";
    }

    @GetMapping("/sales/{memberId}")
    public String itemsRegisteredByThisMember(@PathVariable("memberId") Member salesman, Model model,
                                              @PageableDefault(size = 5, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Item> page = itemRepository.findItems(salesman, pageable);
        Page<ItemDto> dtoPage = page.map(ItemDto::new);
        model.addAttribute("page", dtoPage);
        return "item/item-list";
    }

    /**
     * 상품 등록
     * @param form
     * @return
     */
    private Item saveItem(ItemRegisterForm form, Member salesman) {

        availableSale(salesman);

        return itemService.saveItem(form, salesman);
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

}
