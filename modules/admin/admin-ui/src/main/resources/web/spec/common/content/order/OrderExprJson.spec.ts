module OrderExprJsonSpec {

    import OrderExprJson = api.content.json.OrderExprJson;

    export function getOrderExprJson(): OrderExprJson {
        return <OrderExprJson> {
            direction: 'DESC',
            fieldName: 'modifiedtime'
        };
    }
}
