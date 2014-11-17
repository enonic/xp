module api.content {

    import ChildOrderJson =  api.content.json.ChildOrderJson;
    import OrderExprJson =  api.content.json.OrderExprJson;
    import OrderExprWrapperJson = api.content.json.OrderExprWrapperJson;

    export class ChildOrder {

        private DEFAULT_ORDER_DIRECTION_VALUE: string = "DESC";

        private DEFAULT_ORDER_FIELD_VALUE: string = "modifiedTime";

        private ASC_ORDER_DIRECTION_VALUE: string = "ASC";

        private DESC_ORDER_DIRECTION_VALUE: string = "DESC";

        private MANUAL_ORDER_VALUE_KEY: string = "manualordervalue";

        private orderExpressions: OrderExpr[] = [];

        getOrderExpressions(): OrderExpr[] {
            return this.orderExpressions;
        }

        static fromJson(childOrderJson: ChildOrderJson): ChildOrder {
            var childOrder: ChildOrder = new ChildOrder();
            childOrderJson.orderExpressions.forEach((orderExprJson: OrderExprWrapperJson) => {
                if (orderExprJson.FieldOrderExpr) {
                    childOrder.orderExpressions.push(new FieldOrderExprBuilder(orderExprJson.FieldOrderExpr).build());
                } else if (orderExprJson.DynamicOrderExpr) {
                    childOrder.orderExpressions.push(new DynamicOrderExprBuilder(orderExprJson.DynamicOrderExpr).build());
                }
            });
            return childOrder;
        }

        isManual(): boolean {
            if (this.orderExpressions.length == 0) {
                return false;
            }
            var order = this.orderExpressions[0];
            if (api.ObjectHelper.iFrameSafeInstanceOf(order, FieldOrderExpr)) {
                return api.ObjectHelper.stringEquals(this.MANUAL_ORDER_VALUE_KEY.toLowerCase(),
                    (<FieldOrderExpr>order).getFieldName().toLowerCase());
            }
            return false;
        }

        isDesc(): boolean {
            if (this.orderExpressions.length == 0) {
                return this.DEFAULT_ORDER_DIRECTION_VALUE == this.DESC_ORDER_DIRECTION_VALUE;
            }
            var order = this.orderExpressions[0];
            return api.ObjectHelper.stringEquals(this.DESC_ORDER_DIRECTION_VALUE.toLowerCase(), order.getDirection().toLowerCase());
        }

        isDefault(): boolean {
            var order = this.orderExpressions[0];
            if (api.ObjectHelper.iFrameSafeInstanceOf(order, FieldOrderExpr)) {
                var fieldOrder = (<FieldOrderExpr>order);
                if (api.ObjectHelper.stringEquals(this.DEFAULT_ORDER_DIRECTION_VALUE.toLowerCase(),
                    fieldOrder.getDirection().toLowerCase()) &&
                    api.ObjectHelper.stringEquals(this.DEFAULT_ORDER_FIELD_VALUE.toLowerCase(), fieldOrder.getFieldName().toLowerCase())) {
                    return true;
                }
            }
            return false;
        }

        toJson(): api.content.json.ChildOrderJson {

            return {
                "orderExpressions": OrderExpr.toArrayJson(this.getOrderExpressions())
            };
        }

        static toSetChildOrderJson(content: ContentSummary): api.content.json.SetChildOrderJson {
            return {
                "childOrder": content.getChildOrder().toJson(),
                "contentId": content.getContentId().toString()
            };
        }

    }
}