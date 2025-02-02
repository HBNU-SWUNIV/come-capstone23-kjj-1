package com.hanbat.zanbanzero.service.menu;

import com.hanbat.zanbanzero.dto.menu.MenuUpdateDto;
import com.hanbat.zanbanzero.dto.menu.MenuInfoDto;
import com.hanbat.zanbanzero.entity.menu.Menu;
import com.hanbat.zanbanzero.dto.menu.MenuDto;
import com.hanbat.zanbanzero.entity.menu.MenuInfo;
import com.hanbat.zanbanzero.exception.controller.exceptions.CantFindByIdException;
import com.hanbat.zanbanzero.exception.controller.exceptions.SameNameException;
import com.hanbat.zanbanzero.exception.controller.exceptions.WrongParameter;
import com.hanbat.zanbanzero.repository.menu.MenuInfoRepository;
import com.hanbat.zanbanzero.repository.menu.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuInfoRepository menuInfoRepository;

    @Cacheable(value = "MenuDto", cacheManager = "cacheManager")
    public List<MenuDto> getMenus() {
        List<Menu> menus = menuRepository.findAll();

        return menus.stream()
                .map((menu) -> MenuDto.createMenuDto(menu))
                .collect(Collectors.toList());
    }

    public MenuInfoDto getMenuInfo(Long id) throws CantFindByIdException {
        MenuInfo menu = menuInfoRepository.findByIdAndFetch(id).orElseThrow(CantFindByIdException::new);

        MenuInfoDto result = MenuInfoDto.createMenuDto(menu);
        return result;
    }

    @Transactional
    public void addMenu(MenuUpdateDto dto) throws SameNameException {
        if (menuRepository.existsByName(dto.getName())) {
            throw new SameNameException("데이터 중복입니다.");
        }

        Menu menu = menuRepository.save(dto.toMenu());

        menuInfoRepository.save(dto.toMenuInfo(menu));
    }

    @Transactional
    public void updateMenu(MenuUpdateDto dto, Long id) throws CantFindByIdException {
        Menu menu = menuRepository.findById(id).orElseThrow(CantFindByIdException::new);

        menu.patch(dto);
    }

    @Transactional
    public void updateMenuInfo(MenuUpdateDto dto, Long id) throws CantFindByIdException {
        MenuInfo menu = menuInfoRepository.findById(id).orElseThrow(CantFindByIdException::new);

        menu.patch(dto);
    }

    public void deleteMenu(Long id) throws CantFindByIdException {
        MenuInfo menu = menuInfoRepository.findById(id).orElseThrow(CantFindByIdException::new);

        menuInfoRepository.delete(menu);
    }

    @Transactional
    public void setSoldOut(Long id, String type) throws CantFindByIdException, WrongParameter {
        Menu menu = menuRepository.findById(id).orElseThrow(CantFindByIdException::new);

        switch (type) {
            case "n":
                menu.setSold(false);
                break;
            case "y":
                menu.setSold(true);
                break;
            default:
                throw new WrongParameter("잘못된 파라미터입니다.");
        }
    }
}
