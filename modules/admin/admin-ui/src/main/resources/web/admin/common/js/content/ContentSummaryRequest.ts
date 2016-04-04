module api.content {

    import QueryField = api.query.QueryField;
    import Expression = api.query.expr.Expression;
    import OrderExpr = api.query.expr.OrderExpr;
    import QueryExpr = api.query.expr.QueryExpr;

    export class ContentSummaryRequest extends api.rest.ResourceRequest<json.ContentQueryResultJson<json.ContentSummaryJson>, ContentSummary[]> {

        private contentQuery: query.ContentQuery;

        private request: ContentQueryRequest<json.ContentSummaryJson, ContentSummary>;

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

        setSearchQueryExpr(searchString: string, orderList?: OrderExpr[]) {

            var fulltextExpression: Expression = new api.query.FulltextSearchExpressionBuilder().
                setSearchString(searchString).
                addField(new QueryField(QueryField.DISPLAY_NAME, 5)).
                addField(new QueryField(QueryField.NAME, 3)).
                addField(new QueryField(QueryField.ALL)).
                build();

            var queryExpr: QueryExpr = new QueryExpr(fulltextExpression, orderList);
            this.contentQuery.setQueryExpr(queryExpr);
        }

        setQueryExpr(queryExpr: QueryExpr) {
            this.contentQuery.setQueryExpr(queryExpr);
        }

        private createContentTypeNames(names: string[]): api.schema.content.ContentTypeName[] {
            return (names || []).map((name: string) => new api.schema.content.ContentTypeName(name));
        }
    }
}
