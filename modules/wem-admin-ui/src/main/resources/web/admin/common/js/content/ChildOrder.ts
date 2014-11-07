module api.content {

    import ChildOrderJson =  api.content.json.ChildOrderJson;
    import OrderExprJson =  api.content.json.OrderExprJson;
    import OrderExprWrapperJson = api.content.json.OrderExprWrapperJson;

    export class ChildOrder {

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