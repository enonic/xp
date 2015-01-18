module api.content {

    import ChildOrderJson =  api.content.json.ChildOrderJson;
    import OrderExprJson =  api.content.json.OrderExprJson;
    import OrderExprWrapperJson = api.content.json.OrderExprWrapperJson;

    export class ChildOrder implements api.Equitable {

        private DEFAULT_ORDER_DIRECTION_VALUE: string = "DESC";

        static DEFAULT_ORDER_FIELD_VALUE: string = api.query.QueryField.MODIFIED_TIME;

        static ASC_ORDER_DIRECTION_VALUE: string = "ASC";

        static DESC_ORDER_DIRECTION_VALUE: string = "DESC";

        static MANUAL_ORDER_VALUE_KEY: string = api.query.QueryField.MANUAL_ORDER_VALUE;

        private orderExpressions: OrderExpr[] = [];

        getOrderExpressions(): OrderExpr[] {
            return this.orderExpressions;
        }

        addOrderExpr(expr: OrderExpr) {
            this.orderExpressions.push(expr);
        }

        addOrderExpressions(expressions: OrderExpr[]) {
            expressions.forEach((expr: OrderExpr) => {
                this.orderExpressions.push(expr);
            });

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
                return api.ObjectHelper.stringEquals(ChildOrder.MANUAL_ORDER_VALUE_KEY.toLowerCase(),
                    (<FieldOrderExpr>order).getFieldName().toLowerCase());
            }
            return false;
        }

        isDesc(): boolean {
            if (this.orderExpressions.length == 0) {
                return this.DEFAULT_ORDER_DIRECTION_VALUE == ChildOrder.DESC_ORDER_DIRECTION_VALUE;
            }
            var order = this.orderExpressions[0];
            return api.ObjectHelper.stringEquals(ChildOrder.DESC_ORDER_DIRECTION_VALUE.toLowerCase(), order.getDirection().toLowerCase());
        }

        isDefault(): boolean {
            var order = this.orderExpressions[0];
            if (api.ObjectHelper.iFrameSafeInstanceOf(order, FieldOrderExpr)) {
                var fieldOrder = (<FieldOrderExpr>order);
                if (api.ObjectHelper.stringEquals(this.DEFAULT_ORDER_DIRECTION_VALUE.toLowerCase(),
                    fieldOrder.getDirection().toLowerCase()) &&
                    api.ObjectHelper.stringEquals(ChildOrder.DEFAULT_ORDER_FIELD_VALUE.toLowerCase(),
                        fieldOrder.getFieldName().toLowerCase())) {
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

        toString(): string {
            var result = "";
            this.orderExpressions.forEach((expr: OrderExpr) => {
                result = result.concat(" ", expr.toString());
            });
            return result;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ChildOrder)) {
                return false;
            }
            var other = <ChildOrder>o;
            if (this.orderExpressions.length != other.getOrderExpressions().length) {
                return false;
            }
            for (var count in this.orderExpressions) {
                if (!this.orderExpressions[count].equals(other.getOrderExpressions()[count])) {
                    return false;
                }
            }

            return true;
        }

        static toSetChildOrderJson(contentId: ContentId, childOrder: ChildOrder): api.content.json.SetChildOrderJson {
            if (contentId && childOrder) {
                return {
                    "childOrder": childOrder.toJson(),
                    "contentId": contentId.toString()
                };
            }
        }

    }
}