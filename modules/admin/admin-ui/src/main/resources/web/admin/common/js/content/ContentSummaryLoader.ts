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
        public static DEFAULT_ORDER: OrderExpr[] = [ContentSummaryLoader.MODIFIED_TIME_DESC];

        private contentSummaryRequest: ContentSummaryRequest;

        constructor() {
            this.contentSummaryRequest = new ContentSummaryRequest();

            super(this.contentSummaryRequest);

            debugger;
            this.setSearchQueryExpr();
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

        setContentPath(path: ContentPath) {
            this.contentSummaryRequest.setContentPath(path);
        }

        private setSearchQueryExpr(searchString: string = "") {
            var order = searchString ? ContentSummaryLoader.SCORE_MODIFIED_ORDER : ContentSummaryLoader.DEFAULT_ORDER;
            this.contentSummaryRequest.setSearchQueryExpr(searchString, order);
        }

        search(searchString: string): wemQ.Promise<ContentSummary[]> {
            this.setSearchQueryExpr(searchString);

            return this.load();
        }

        sendRequest(): wemQ.Promise<ContentSummary[]> {
            return this.contentSummaryRequest.sendAndParse();
        }

    }


}