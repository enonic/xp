module api.content {

    import QueryField = api.query.QueryField;
    import Expression = api.query.expr.Expression;
    import OrderExpr = api.query.expr.OrderExpr;
    import QueryExpr = api.query.expr.QueryExpr;
    import FieldExpr = api.query.expr.FieldExpr;
    import FieldOrderExpr = api.query.expr.FieldOrderExpr;
    import OrderDirection = api.query.expr.OrderDirection;

    export class ContentSummaryRequest extends api.rest.ResourceRequest<json.ContentQueryResultJson<json.ContentSummaryJson>, ContentSummary[]> {

        private contentQuery: query.ContentQuery;

        private path: ContentPath;

        private request: ContentQueryRequest<json.ContentSummaryJson, ContentSummary>;

        public static MODIFIED_TIME_DESC = new FieldOrderExpr(new FieldExpr("modifiedTime"), OrderDirection.DESC);

        public static SCORE_DESC = new FieldOrderExpr(new FieldExpr("_score"), OrderDirection.DESC);

        public static DEFAULT_ORDER: OrderExpr[] = [ContentSummaryRequest.SCORE_DESC, ContentSummaryRequest.MODIFIED_TIME_DESC];

        constructor() {
            super();
            this.contentQuery = new query.ContentQuery();
            this.request = new ContentQueryRequest<json.ContentSummaryJson, ContentSummary>(this.contentQuery).
                setExpand(api.rest.Expand.SUMMARY);
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
            return this.request.send();
        }

        sendAndParse(): wemQ.Promise<ContentSummary[]> {

            return this.request.sendAndParse().
                then((queryResult: ContentQueryResult<ContentSummary,json.ContentSummaryJson>) => {
                    return queryResult.getContents();
                });
        }

        setAllowedContentTypes(contentTypes: string[]) {
            this.contentQuery.setContentTypeNames(this.createContentTypeNames(contentTypes));
        }

        setAllowedContentTypeNames(contentTypeNames: api.schema.content.ContentTypeName[]) {
            this.contentQuery.setContentTypeNames(contentTypeNames);
        }

        setSize(size: number) {
            this.contentQuery.setSize(size);
        }

        setContentPath(path: ContentPath) {
            this.path = path;
            this.setSearchQueryExpr();
        }

        setSearchQueryExpr(searchString: string = "") {
            var fulltextExpression = this.createSearchExpression(searchString);

            this.contentQuery.setQueryExpr(new QueryExpr(fulltextExpression, ContentSummaryRequest.DEFAULT_ORDER));
        }

        private createSearchExpression(searchString): Expression {
            return new api.query.PathMatchExpressionBuilder()
                .setSearchString(searchString)
                .setPath(this.path ? this.path.toString() : "")
                .addField(new QueryField(QueryField.DISPLAY_NAME, 5))
                .addField(new QueryField(QueryField.NAME, 3))
                .addField(new QueryField(QueryField.ALL))
                .build();
        }

        setQueryExpr(queryExpr: QueryExpr) {
            this.contentQuery.setQueryExpr(queryExpr);
        }

        private createContentTypeNames(names: string[]): api.schema.content.ContentTypeName[] {
            return (names || []).map((name: string) => new api.schema.content.ContentTypeName(name));
        }
    }
}
