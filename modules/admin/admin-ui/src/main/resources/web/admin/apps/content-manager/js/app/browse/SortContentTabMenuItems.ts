module app.browse {

    import ChildOrder = api.content.ChildOrder;
    import FieldOrderExpr = api.content.FieldOrderExpr;
    import FieldOrderExprBuilder = api.content.FieldOrderExprBuilder;

    export class SortContentTabMenuItems {

        public SORT_ASC_DISPALAY_NAME_ITEM: SortContentTabMenuItem;
        public SORT_DESC_DISPALAY_NAME_ITEM: SortContentTabMenuItem;
        public SORT_ASC_MODIFIED_ITEM: SortContentTabMenuItem;
        public SORT_DESC_MODIFIED_ITEM: SortContentTabMenuItem;
        public SORT_MANUAL_ITEM: SortContentTabMenuItem;

        private items: SortContentTabMenuItem[] = [];

        constructor() {
            var order = new ChildOrder();
            order.addOrderExpr(new FieldOrderExprBuilder().
                setFieldName(api.query.QueryField.DISPLAY_NAME).
                setDirection(ChildOrder.ASC_ORDER_DIRECTION_VALUE).
                build());
            this.SORT_ASC_DISPALAY_NAME_ITEM = new SortContentTabMenuItemBuilder().setLabel("DisplayName - Ascending").
                setChildOrder(order).build();

            order = new ChildOrder();
            order.addOrderExpr(new FieldOrderExprBuilder().
                setFieldName(api.query.QueryField.DISPLAY_NAME).
                setDirection(ChildOrder.DESC_ORDER_DIRECTION_VALUE).
                build());
            this.SORT_DESC_DISPALAY_NAME_ITEM = new SortContentTabMenuItemBuilder().setLabel("DisplayName - Descending").
                setChildOrder(order).build();

            order = new ChildOrder();
            order.addOrderExpr(new FieldOrderExprBuilder().
                setFieldName(api.query.QueryField.MODIFIED_TIME).
                setDirection(ChildOrder.ASC_ORDER_DIRECTION_VALUE).
                build());
            this.SORT_ASC_MODIFIED_ITEM = new SortContentTabMenuItemBuilder().setLabel("Modified - Ascending").
                setChildOrder(order).build();

            order = new ChildOrder();
            order.addOrderExpr(new FieldOrderExprBuilder().
                setFieldName(api.query.QueryField.MODIFIED_TIME).
                setDirection(ChildOrder.DESC_ORDER_DIRECTION_VALUE).
                build());
            this.SORT_DESC_MODIFIED_ITEM = new SortContentTabMenuItemBuilder().setLabel("Modified - Descending (default)").
                setChildOrder(order).build();

            order = new ChildOrder();
            order.addOrderExpr(new FieldOrderExprBuilder().
                setFieldName(api.query.QueryField.MANUAL_ORDER_VALUE).
                setDirection(ChildOrder.DESC_ORDER_DIRECTION_VALUE).
                build());
            this.SORT_MANUAL_ITEM = new SortContentTabMenuItemBuilder().setLabel("Manually Sorted").
                setChildOrder(order).build();

            this.items.push(this.SORT_ASC_DISPALAY_NAME_ITEM, this.SORT_DESC_DISPALAY_NAME_ITEM, this.SORT_ASC_MODIFIED_ITEM,
                this.SORT_DESC_MODIFIED_ITEM, this.SORT_MANUAL_ITEM);
        }

        getAllItems(): SortContentTabMenuItem[] {
            return this.items.slice(0, this.items.length);
        }

    }
}
