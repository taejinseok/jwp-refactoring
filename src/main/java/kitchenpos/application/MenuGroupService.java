package kitchenpos.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kitchenpos.domain.MenuGroup;
import kitchenpos.repository.MenuGroupRepository;
import kitchenpos.ui.dto.MenuGroupCreateRequest;
import kitchenpos.ui.dto.MenuGroupResponse;

@Service
public class MenuGroupService {

    private final MenuGroupRepository menuGroupDao;

    public MenuGroupService(final MenuGroupRepository menuGroupDao) {
        this.menuGroupDao = menuGroupDao;
    }

    @Transactional
    public MenuGroupResponse create(final MenuGroupCreateRequest createRequest) {
        MenuGroup createdMenuGroup = menuGroupDao.save(createRequest.toEntity());
        return MenuGroupResponse.of(createdMenuGroup);
    }

    public List<MenuGroupResponse> list() {
        List<MenuGroup> menuGroups = menuGroupDao.findAll();
        return MenuGroupResponse.listOf(menuGroups);
    }
}
