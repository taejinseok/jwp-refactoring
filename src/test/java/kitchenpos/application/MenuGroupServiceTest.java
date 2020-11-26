package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import kitchenpos.ui.dto.MenuGroupCreateRequest;
import kitchenpos.ui.dto.MenuGroupResponse;

@SuppressWarnings("NonAsciiCharacters")
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class MenuGroupServiceTest {

    @Autowired
    private MenuGroupService menuGroupService;

    @DisplayName("create: 메뉴 그룹 생성 요청시, 입력 받은 이름을 기반으로 생성 하면, ID 생성 및 입력 값을 통해 생성된다.")
    @Test
    void create() {
        MenuGroupResponse 추가하고자하는메뉴그룹 = menuGroupService.create(new MenuGroupCreateRequest("세트 그룹"));

        assertAll(
                () -> assertThat(추가하고자하는메뉴그룹.getId()).isNotNull(),
                () -> assertThat(추가하고자하는메뉴그룹.getName()).isEqualTo("세트 그룹")
        );
    }

    @DisplayName("list: 현재 저장 되어 있는 메뉴그룹의 목록을 반환한다.")
    @Test
    void list() {
        menuGroupService.create(new MenuGroupCreateRequest("치킨세트"));
        menuGroupService.create(new MenuGroupCreateRequest("치킨단품"));

        List<MenuGroupResponse> 전체메뉴그룹목록 = menuGroupService.list();

        assertThat(전체메뉴그룹목록).hasSize(2);
    }
}