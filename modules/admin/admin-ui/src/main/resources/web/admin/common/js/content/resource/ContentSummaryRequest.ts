module api.content.resource {

    import QueryField = api.query.QueryField;
    import OrderExpr = api.query.expr.OrderExpr;
    import QueryExpr = api.query.expr.QueryExpr;
    import FieldExpr = api.query.expr.FieldExpr;
    import FieldOrderExpr = api.query.expr.FieldOrderExpr;
    import OrderDirection = api.query.expr.OrderDirection;
    import ConstraintExpr = api.query.expr.ConstraintExpr;

    export class ContentSummaryRequest extends api.rest.ResourceRequest<json.ContentQueryResultJson<json.ContentSummaryJson>, ContentSummary[]> {

        private path: ContentPath;

        private searchString: string = "";

        private request: ContentQueryRequest<json.ContentSummaryJson, ContentSummary>;

        public static MODIFIED_TIME_DESC = new FieldOrderExpr(new FieldExpr("modifiedTime"), OrderDirection.DESC);

        public static SCORE_DESC = new FieldOrderExpr(new FieldExpr("_score"), OrderDirection.DESC);

        public static PATH_ASC = new FieldOrderExpr(new FieldExpr("_path"), OrderDirection.ASC);

        public static DEFAULT_ORDER: OrderExpr[] = [ContentSummaryRequest.SCORE_DESC, ContentSummaryRequest.MODIFIED_TIME_DESC];

        constructor() {
            super();
            this.request =
                new ContentQueryRequest<json.ContentSummaryJson, ContentSummary>(new query.ContentQuery()).setExpand(api.rest.Expand.SUMMARY);
        }

        getSearchString(): string {
            return this.searchString;
        }

        getRestPath(): api.rest.Path {
            return this.request.getRestPath();
        }

        getRequestPath(): api.rest.Path {
            return this.request.getRequestPath();
        }

        getContentPath(): ContentPath {
            return this.path;
        }

        getParams(): Object {
            return this.request.getParams();
        }

        send(): wemQ.Promise<api.rest.JsonResponse<json.ContentQueryResultJson<json.ContentSummaryJson>>> {
            this.buildSearchQueryExpr();

            return this.request.send();
        }

        sendAndParse(): wemQ.Promise<ContentSummary[]> {
            this.buildSearchQueryExpr();

            return this.request.sendAndParse().then(
                (queryResult: api.content.resource.result.ContentQueryResult<ContentSummary,json.ContentSummaryJson>) => {
                    return queryResult.getContents();
                });
        }

        setAllowedContentTypes(contentTypes: string[]) {
            this.request.getContentQuery().setContentTypeNames(this.createContentTypeNames(contentTypes));
        }

        setAllowedContentTypeNames(contentTypeNames: api.schema.content.ContentTypeName[]) {
            this.request.getContentQuery().setContentTypeNames(contentTypeNames);
        }

        setSize(size: number) {
            this.request.getContentQuery().setSize(size);
        }

        setContentPath(path: ContentPath) {
            this.path = path;
        }

        setSearchString(value: string = "") {
            this.searchString = value;
        }

        isPartiallyLoaded(): boolean {
            return this.request.isPartiallyLoaded();
        }

        resetParams() {
            this.request.resetParams();
        }

        private buildSearchQueryExpr() {
            this.request.getContentQuery().setQueryExpr(new QueryExpr(this.createSearchExpression(), this.getDefaultOrder()));
        }

        protected getDefaultOrder(): OrderExpr[] {
            return ContentSummaryRequest.DEFAULT_ORDER;
        }

        protected createSearchExpression(): ConstraintExpr {
            return new api.query.PathMatchExpressionBuilder()
                .setSearchString(this.searchString)
                .setPath(this.path ? this.path.toString() : "")
                .addField(new QueryField(QueryField.DISPLAY_NAME, 5))
                .addField(new QueryField(QueryField.NAME, 3))
                .addField(new QueryField(QueryField.ALL))
                .build();
        }

        private createContentTypeNames(names: string[]): api.schema.content.ContentTypeName[] {
            return (names || []).map((name: string) => new api.schema.content.ContentTypeName(name));
        }
    }
}
