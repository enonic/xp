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
    import BatchContentResult = api.content.resource.result.BatchContentResult;
    import ContentState = api.schema.content.ContentState;
    import ContentTypeName = api.schema.content.ContentTypeName;

    export class ContentTreeSelectorQueryRequest extends ContentResourceRequest<ContentTreeSelectorItemJson[], ContentTreeSelectorItem[]> {

        public static DEFAULT_SIZE: number = 15;

        public static MODIFIED_TIME_DESC: FieldOrderExpr = new FieldOrderExpr(new FieldExpr('modifiedTime'), OrderDirection.DESC);

        public static SCORE_DESC: FieldOrderExpr = new FieldOrderExpr(new FieldExpr('_score'), OrderDirection.DESC);

        public static DEFAULT_ORDER: OrderExpr[] = [ContentTreeSelectorQueryRequest.SCORE_DESC,
            ContentTreeSelectorQueryRequest.MODIFIED_TIME_DESC];

        private queryExpr: api.query.expr.QueryExpr;

        private from: number = 0;

        private size: number = -1;//ContentTreeSelectorQueryRequest.DEFAULT_SIZE;

        private expand: api.rest.Expand = api.rest.Expand.SUMMARY;

        private content: ContentSummary;

        private inputName: string;

        private contentTypeNames: string[] = [];

        private allowedContentPaths: string[] = [];

        private relationshipType: string;

        private loaded: boolean;

        private results: ContentSummary[] = [];

        private parentPath: ContentPath;

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

        setParentPath(parentPath: ContentPath) {
            this.parentPath = parentPath;
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
            return api.rest.Path.fromParent(super.getResourcePath(), 'treeSelectorQuery');
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
                contentId: this.content.getId().toString(),
                inputName: this.getInputName(),
                contentTypeNames: this.contentTypeNames,
                allowedContentPaths: this.allowedContentPaths,
                relationshipType: this.relationshipType,
                parentPath: this.parentPath ? this.parentPath.toString() : null
            };
        }

        sendAndParse(): wemQ.Promise<ContentTreeSelectorItem[]> {
            return this.send().then((response: api.rest.JsonResponse<ContentTreeSelectorItemJson[]>) => {
                if (response.getResult() && response.getResult().length > 0) {
                    return response.getResult().map(json => ContentTreeSelectorItem.fromJson(json));
                } else {
                    return [];
                }
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
    export class ContentTreeSelectorItemJson {

        content: ContentSummaryJson;

        expand: boolean;
    }

    export class ContentTreeSelectorItem {

        private content: ContentSummary;

        private expand: boolean;

        constructor(content: ContentSummary, expand: boolean) {
            this.content = content;
            this.expand = expand;
        }

        public static fromJson(json: ContentTreeSelectorItemJson) {
            return new ContentTreeSelectorItem(ContentSummary.fromJson(json.content), json.expand);
        }

        getContent(): ContentSummary {
            return this.content;
        }

        getId(): string {
            return this.content.getId();
        }

        getContentId(): ContentId {
            return this.content.getContentId();
        }

        getPath(): ContentPath {
            return this.content.getPath();
        }

        getName(): ContentName {
            return this.content.getName();
        }

        getDisplayName(): string {
            return this.content.getDisplayName();
        }

        getContentState(): ContentState {
            return this.content.getContentState();
        }

        hasChildren(): boolean {
            return this.content.hasChildren();
        }

        isValid(): boolean {
            return this.content.isValid();
        }

        getIconUrl(): string {
            return this.content.getIconUrl();
        }

        getType(): ContentTypeName {
            return this.content.getType();
        }

        isImage(): boolean {
            return this.content.isImage();
        }

        isSite(): boolean {
            return this.content.isSite();
        }

        getExpand(): boolean {
            return this.expand;
        }
    }
}
