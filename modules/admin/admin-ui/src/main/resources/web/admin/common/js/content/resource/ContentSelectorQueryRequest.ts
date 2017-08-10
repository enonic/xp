module api.content.resource {

    import OrderExpr = api.query.expr.OrderExpr;
    import FieldOrderExpr = api.query.expr.FieldOrderExpr;
    import OrderDirection = api.query.expr.OrderDirection;
    import FieldExpr = api.query.expr.FieldExpr;
    import Expression = api.query.expr.Expression;
    import QueryField = api.query.QueryField;
    import QueryExpr = api.query.expr.QueryExpr;
    import ContentSummaryJson = api.content.json.ContentSummaryJson;
    import ContentId = api.content.ContentId;
    import ContentQueryResultJson = api.content.json.ContentQueryResultJson;

    export class ContentSelectorQueryRequest extends ContentResourceRequest<ContentQueryResultJson<ContentSummaryJson>, ContentSummary[]> {

        public static DEFAULT_SIZE: number = 15;

        public static MODIFIED_TIME_DESC: FieldOrderExpr = new FieldOrderExpr(new FieldExpr('modifiedTime'), OrderDirection.DESC);

        public static SCORE_DESC: FieldOrderExpr = new FieldOrderExpr(new FieldExpr('_score'), OrderDirection.DESC);

        public static DEFAULT_ORDER: OrderExpr[] = [ContentSelectorQueryRequest.SCORE_DESC, ContentSelectorQueryRequest.MODIFIED_TIME_DESC];

        private queryExpr: api.query.expr.QueryExpr;

        private from: number = 0;

        private size: number = ContentSelectorQueryRequest.DEFAULT_SIZE;

        private expand: api.rest.Expand = api.rest.Expand.SUMMARY;

        private content: ContentSummary;

        private inputName: string;

        private contentTypeNames: string[] = [];

        private allowedContentPaths: string[] = [];

        private relationshipType: string;

        private loaded: boolean;

        private results: ContentSummary[] = [];

        constructor() {
            super();
            super.setMethod('POST');

            this.setQueryExpr();
        }

        setInputName(name: string) {
            this.inputName = name;
        }

        getInputName(): string {
            return this.inputName;
        }

        setContent(content: ContentSummary) {
            this.content = content;
            this.setQueryExpr();
        }

        getContent(): ContentSummary {
            return this.content;
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

        setContentTypeNames(contentTypeNames: string[]) {
            this.contentTypeNames = contentTypeNames;
        }

        setAllowedContentPaths(allowedContentPaths: string[]) {
            this.allowedContentPaths = allowedContentPaths;
        }

        setRelationshipType(relationshipType: string) {
            this.relationshipType = relationshipType;
        }

        setQueryExpr(searchString: string = '') {
            let fulltextExpression = this.createSearchExpression(searchString);

            this.queryExpr = new QueryExpr(fulltextExpression, ContentSelectorQueryRequest.DEFAULT_ORDER);
        }

        private createSearchExpression(searchString: string): Expression {
            return new api.query.PathMatchExpressionBuilder()
                .setSearchString(searchString)
                .setPath(this.content ? this.content.getPath().toString() : '')
                .addField(new QueryField(QueryField.DISPLAY_NAME, 5))
                .addField(new QueryField(QueryField.NAME, 3))
                .addField(new QueryField(QueryField.ALL))
                .build();
        }

        getQueryExpr(): api.query.expr.QueryExpr {
            return this.queryExpr;
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'selectorQuery');
        }

        isPartiallyLoaded(): boolean {
            return this.results.length > 0 && !this.loaded;
        }

        isLoaded(): boolean {
            return this.loaded;
        }

        resetParams() {
            this.from = 0;
            this.loaded = false;
        }

        getParams(): Object {
            let queryExprAsString = this.getQueryExpr() ? this.getQueryExpr().toString() : '';

            return {
                queryExpr: queryExprAsString,
                from: this.getFrom(),
                size: this.getSize(),
                expand: this.expandAsString(),
                contentId: this.content ? this.content.getId().toString() : null,
                inputName: this.getInputName(),
                contentTypeNames: this.contentTypeNames,
                allowedContentPaths: this.allowedContentPaths,
                relationshipType: this.relationshipType
            };
        }

        sendAndParse(): wemQ.Promise<ContentSummary[]> {

            return this.send().then((response: api.rest.JsonResponse<ContentQueryResultJson<ContentSummaryJson>>) => {

                let responseResult: ContentQueryResultJson<ContentSummaryJson> = response.getResult();

                let contentsAsJson: ContentSummaryJson[] = responseResult.contents;

                let contentSummaries: ContentSummary[] = <any[]> this.fromJsonToContentSummaryArray(
                    <ContentSummaryJson[]>contentsAsJson);

                if (this.from === 0) {
                    this.results = [];
                }
                this.from += responseResult.metadata['hits'];
                this.loaded = this.from >= responseResult.metadata['totalHits'];

                this.results = this.results.concat(contentSummaries);

                return this.results;
            });
        }

        private expandAsString(): string {
            switch (this.expand) {
            case api.rest.Expand.FULL:
                return 'full';
            case api.rest.Expand.SUMMARY:
                return 'summary';
            case api.rest.Expand.NONE:
                return 'none';
            default:
                return 'summary';
            }
        }
    }
}
