module api.content {

    import OrderExpr = api.query.expr.OrderExpr;
    import FieldOrderExpr = api.query.expr.FieldOrderExpr;
    import OrderDirection = api.query.expr.OrderDirection;
    import FieldExpr = api.query.expr.FieldExpr;

    export class ContentSummaryLoader extends ContentSummaryPreLoader {

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

        preLoad(ids: string): wemQ.Promise<ContentSummary[]> {
            this.notifyLoadingData(false);

            let contentIds = ids.split(";").map((id) => {
                return new api.content.ContentId(id);
            });
            return new GetContentSummaryByIds(contentIds).
            get().
            then((results: ContentSummary[]) => {
                if (this.getComparator()) {
                    this.setResults(results.sort(this.getComparator().compare));
                } else {
                    this.setResults(results);
                }
                this.notifyLoadedData(results);
                return this.getResults();
            });
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

        search(searchString: string): wemQ.Promise<ContentSummary[]> {

            this.contentSummaryRequest.setQueryExpr(searchString, this.order);

            return this.load();
        }

        sendRequest(): wemQ.Promise<ContentSummary[]> {
            return this.contentSummaryRequest.sendAndParse();
        }

    }


}