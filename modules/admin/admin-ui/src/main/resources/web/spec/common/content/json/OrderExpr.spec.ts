module OrderExprSpec {

    import OrderExpr = api.content.order.OrderExpr;
    import OrderExprBuilder = api.content.order.OrderExprBuilder;

    export function getOrderExpr(): OrderExpr {
        return new OrderExprBuilder(OrderExprJsonSpec.getOrderExprJson()).build();
    }
}