module api.content {

    import OrderExpr = api.query.expr.OrderExpr;
    import FieldOrderExpr = api.query.expr.FieldOrderExpr;
    import OrderDirection = api.query.expr.OrderDirection;
    import FieldExpr = api.query.expr.FieldExpr;
    import QueryExpr = api.query.expr.QueryExpr;

    export class ContentSummaryLoader extends ContentSummaryPreLoader {

        public static MODIFIED_TIME_DESC = new FieldOrderExpr(new FieldExpr("modifiedTime"), OrderDirection.DESC);
        public static SCORE = new FieldOrderExpr(new FieldExpr("_score"), OrderDirection.DESC);

        public static SCORE_MODIFIED_ORDER: OrderExpr[] = [ContentSummaryLoader.SCORE, ContentSummaryLoader.MODIFIED_TIME_DESC];

        private contentSummaryRequest: ContentSummaryRequest;

        private order: OrderExpr[];

        constructor() {
            this.contentSummaryRequest = new ContentSummaryRequest();

            // Setting default order
            this.order = ContentSummaryLoader.SCORE_MODIFIED_ORDER;
            super(this.contentSummaryRequest);
        }

        setOrder(orderList: OrderExpr[]) {
            this.order = orderList;
        }

        setAllowedContentTypes(contentTypes: string[]) {
            this.contentSummaryRequest.setAllowedContentTypes(contentTypes);
        }

        setAllowedContentTypeNames(contentTypeNames: api.schema.content.ContentTypeName[]) {
            this.contentSummaryRequest.setAllowedContentTypeNames(contentTypeNames);
        }

        setSize(size: number) {
            this.contentSummaryRequest.setSize(size);
        }

        setQueryExpr(queryExpr: QueryExpr) {
            this.contentSummaryRequest.setQueryExpr(queryExpr);
        }

        search(searchString: string): wemQ.Promise<ContentSummary[]> {

            this.contentSummaryRequest.setSearchQueryExpr(searchString, this.order);

            return this.load();
        }

        sendRequest(): wemQ.Promise<ContentSummary[]> {
            return this.contentSummaryRequest.sendAndParse();
        }

    }


}