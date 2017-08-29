import '../../api.ts';
import {SortContentTabMenuItem, SortContentTabMenuItemBuilder} from './SortContentTabMenuItem';

import ChildOrder = api.content.order.ChildOrder;
import QueryField = api.query.QueryField;
import FieldOrderExpr = api.content.order.FieldOrderExpr;
import FieldOrderExprBuilder = api.content.order.FieldOrderExprBuilder;
import i18n = api.util.i18n;

interface OrderMeta {
    field: string;
    direction: string;
}

export class SortContentTabMenuItems {

    public SORT_ASC_DISPALAY_NAME_ITEM: SortContentTabMenuItem;
    public SORT_DESC_DISPALAY_NAME_ITEM: SortContentTabMenuItem;
    public SORT_ASC_MODIFIED_ITEM: SortContentTabMenuItem;
    public SORT_DESC_MODIFIED_ITEM: SortContentTabMenuItem;
    public SORT_MANUAL_ITEM: SortContentTabMenuItem;

    private items: SortContentTabMenuItem[] = [];

    constructor() {
        const createOrder = (name, orders: OrderMeta[]) => {
            const order = new ChildOrder();

            orders.forEach((meta: OrderMeta) => {
                order.addOrderExpr(new FieldOrderExprBuilder().setFieldName(meta.field).setDirection(meta.direction).build());
            });

            return new SortContentTabMenuItemBuilder().setLabel(name).setChildOrder(order).build();
        };

        this.SORT_ASC_DISPALAY_NAME_ITEM =
            createOrder(i18n('field.sortType.displayNameAsc'),
                [{field: QueryField.DISPLAY_NAME, direction: ChildOrder.ASC_ORDER_DIRECTION_VALUE}]);
        this.SORT_DESC_DISPALAY_NAME_ITEM =
            createOrder(i18n('field.sortType.displayNameDesc'),
                [{field: QueryField.DISPLAY_NAME, direction: ChildOrder.DESC_ORDER_DIRECTION_VALUE}]);
        this.SORT_ASC_MODIFIED_ITEM =
            createOrder(i18n('field.sortType.modifiedAsc'),
                [{field: QueryField.MODIFIED_TIME, direction: ChildOrder.ASC_ORDER_DIRECTION_VALUE}]);
        this.SORT_DESC_MODIFIED_ITEM =
            createOrder(i18n('field.sortType.modifiedDesc'),
                [{field: QueryField.MODIFIED_TIME, direction: ChildOrder.DESC_ORDER_DIRECTION_VALUE}]);
        this.SORT_MANUAL_ITEM =
            createOrder(i18n('field.sortType.manual'), [
                {field: QueryField.MANUAL_ORDER_VALUE, direction: ChildOrder.DESC_ORDER_DIRECTION_VALUE},
                {field: QueryField.TIMESTAMP, direction: ChildOrder.DESC_ORDER_DIRECTION_VALUE}
            ]);

        this.items.push(this.SORT_ASC_DISPALAY_NAME_ITEM, this.SORT_DESC_DISPALAY_NAME_ITEM, this.SORT_ASC_MODIFIED_ITEM,
            this.SORT_DESC_MODIFIED_ITEM, this.SORT_MANUAL_ITEM);
    }

    getAllItems(): SortContentTabMenuItem[] {
        return this.items.slice();
    }

}
