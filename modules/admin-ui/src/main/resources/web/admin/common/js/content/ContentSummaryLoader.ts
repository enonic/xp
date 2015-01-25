module api.content {

    import OrderExpr = api.query.expr.OrderExpr;
    import FieldOrderExpr = api.query.expr.FieldOrderExpr;
    import OrderDirection = api.query.expr.OrderDirection;
    import FieldExpr = api.query.expr.FieldExpr;

    export class ContentSummaryLoader extends api.util.loader.BaseLoader<json.ContentQueryResultJson<json.ContentSummaryJson>, ContentSummary> {

        public static MODIFIED_TIME_DESC = new FieldOrderExpr(new FieldExpr("_modifiedTime"), OrderDirection.DESC);

        public static ORDER_BY_MODIFIED_TIME_DESC: OrderExpr[] = [ContentSummaryLoader.MODIFIED_TIME_DESC];

        private contentSummaryRequest: ContentSummaryRequest;

        private order: OrderExpr[];

        constructor() {
            this.contentSummaryRequest = new ContentSummaryRequest();

            // Setting default order
            this.order = ContentSummaryLoader.ORDER_BY_MODIFIED_TIME_DESC;
            super(this.contentSummaryRequest);
        }

        setOrder(orderList: OrderExpr[]) {
            this.order = orderList;
        }

        setAllowedContentTypes(contentTypes: string[]) {
            this.contentSummaryRequest.setAllowedContentTypes(contentTypes);
        }

        setSize(size: number) {
            this.contentSummaryRequest.setSize(size);
        }

        search(searchString: string): wemQ.Promise<ContentSummary[]> {

            this.contentSummaryRequest.setQueryExpr(searchString, this.order);

            return this.load();
        }

        sendRequest(): wemQ.Promise<ContentSummary[]> {
            return this.contentSummaryRequest.sendAndParse();
        }

    }


}