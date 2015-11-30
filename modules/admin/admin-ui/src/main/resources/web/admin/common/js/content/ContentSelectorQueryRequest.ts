module api.content {

    import OrderExpr = api.query.expr.OrderExpr;
    import FieldOrderExpr = api.query.expr.FieldOrderExpr;
    import OrderDirection = api.query.expr.OrderDirection;
    import FieldExpr = api.query.expr.FieldExpr;
    import Expression = api.query.expr.Expression;
    import QueryField = api.query.QueryField;
    import QueryExpr = api.query.expr.QueryExpr;
    import ContentSummaryJson = api.content.json.ContentSummaryJson;
    import ContentId = api.content.ContentId;

    export class ContentSelectorQueryRequest extends ContentResourceRequest<json.ContentQueryResultJson<ContentSummaryJson>, ContentSummary[]> {

        public static DEFAULT_SIZE = 100;

        public static MODIFIED_TIME_DESC = new FieldOrderExpr(new FieldExpr("_modifiedTime"), OrderDirection.DESC);

        public static ORDER_BY_MODIFIED_TIME_DESC: OrderExpr[] = [ContentSelectorQueryRequest.MODIFIED_TIME_DESC];

        private order: OrderExpr[] = ContentSelectorQueryRequest.ORDER_BY_MODIFIED_TIME_DESC;

        private queryExpr: api.query.expr.QueryExpr;

        private from: number = 0;

        private size: number = ContentSelectorQueryRequest.DEFAULT_SIZE;

        private expand: api.rest.Expand = api.rest.Expand.SUMMARY;

        private id: ContentId;

        private inputName: string;

        constructor() {
            super();
            super.setMethod("POST");
        }

        setInputName(name: string) {
            this.inputName = name;
        }

        getInputName(): string {
            return this.inputName;
        }

        setId(id: ContentId) {
            this.id = id;
        }

        getId(): ContentId {
            return this.id;
        }

        setFrom(from: number) {
            this.from = from;
        }

        getFrom(): number {
            return this.from;
        }

        setSize(size: number) {
            this.size = size;
        }

        getSize(): number {
            return this.size;
        }

        setQueryExpr(searchString: string) {

            var fulltextExpression: Expression = new api.query.FulltextSearchExpressionBuilder().
                setSearchString(searchString).
                addField(new QueryField(QueryField.DISPLAY_NAME, 5)).
                addField(new QueryField(QueryField.NAME, 3)).
                addField(new QueryField(QueryField.ALL)).
                build();

            this.queryExpr = new QueryExpr(fulltextExpression, this.order);
        }

        getQueryExpr(): api.query.expr.QueryExpr {
            return this.queryExpr;
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "selectorQuery");
        }

        getParams(): Object {

            var queryExprAsString = this.getQueryExpr() ? this.getQueryExpr().toString() : "";

            return {
                queryExpr: queryExprAsString,
                from: this.getFrom(),
                size: this.getSize(),
                expand: this.expandAsString(),
                contentId: this.getId().toString(),
                inputName: this.getInputName()
            };
        }

        sendAndParse(): wemQ.Promise<ContentSummary[]> {

            return this.send().
                then((response: api.rest.JsonResponse<json.ContentQueryResultJson<ContentSummaryJson>>) => {

                    var responseResult: json.ContentQueryResultJson<ContentSummaryJson> = response.getResult();

                    var contentsAsJson: json.ContentSummaryJson[] = responseResult.contents;

                    var contentSummaries: ContentSummary[] = <any[]> this.fromJsonToContentSummaryArray(<json.ContentSummaryJson[]>contentsAsJson);

                    return contentSummaries;
                });
        }

        private expandAsString(): string {
            switch (this.expand) {
            case api.rest.Expand.FULL:
                return "full";
            case api.rest.Expand.SUMMARY:
                return "summary";
            case api.rest.Expand.NONE:
                return "none";
            default:
                return "summary";
            }
        }
    }
}