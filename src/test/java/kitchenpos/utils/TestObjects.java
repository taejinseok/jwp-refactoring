package kitchenpos.utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.Product;
import kitchenpos.domain.TableGroup;

public class TestObjects {
    public static Product createProduct(String name, BigDecimal price) {
        return new Product(name, price);
    }

    public static MenuGroup createMenuGroup(String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }

    public static OrderTable createTable(Long tableGroupId, int numberOfGuest, boolean empty) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setTableGroupId(tableGroupId);
        orderTable.setNumberOfGuests(numberOfGuest);
        orderTable.setEmpty(empty);
        return orderTable;
    }

    // public static Menu createMenu(String name, BigDecimal price, Long menuGroupId, List<MenuProduct> menuProducts) {
    //     final Menu menu = new Menu();
    //     menu.setName(name);
    //     menu.setPrice(price);
    //     menu.setMenuGroup(menuGroupId);
    //     menu.setMenuProducts(menuProducts);
    //     return menu;
    // }

    // public static MenuProduct createMenuProduct(Long menuId, Long productId, long quantity) {
    //     final MenuProduct menuProduct = new MenuProduct(savedMenu, product, quantity);
    //     menuProduct.setMenu(menuId);
    //     menuProduct.setProduct(productId);
    //     menuProduct.setQuantity(quantity);
    //     return menuProduct;
    // }

    public static Order createOrder(Long tableId, OrderStatus orderStatus) {
        return createOrder(tableId, LocalDateTime.now(), orderStatus, null);
    }

    public static Order createOrder(Long tableId, LocalDateTime orderTime, OrderStatus orderStatus,
            List<OrderLineItem> orderLineItems) {
        final Order order = new Order();
        order.setOrderTableId(tableId);
        order.setOrderedTime(orderTime);
        order.setOrderStatus(orderStatus.name());
        order.setOrderLineItems(orderLineItems);
        return order;
    }

    public static OrderLineItem createOrderLineItem(Long orderId, Long menuId, long quantity) {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setOrderId(orderId);
        orderLineItem.setMenuId(menuId);
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }

    public static TableGroup createTableGroup(LocalDateTime createdDate) {
        return createTableGroup(createdDate, null);
    }

    public static TableGroup createTableGroup(LocalDateTime createdDate, List<OrderTable> tables) {
        final TableGroup tableGroup = new TableGroup();
        tableGroup.setCreatedDate(createdDate);
        tableGroup.setOrderTables(tables);
        return tableGroup;
    }
}
