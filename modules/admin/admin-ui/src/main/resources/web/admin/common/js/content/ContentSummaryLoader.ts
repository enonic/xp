module api.content {

    import QueryExpr = api.query.expr.QueryExpr;

    export class ContentSummaryLoader extends ContentSummaryPreLoader {

        private contentSummaryRequest: ContentSummaryRequest;

        constructor() {
            this.contentSummaryRequest = new ContentSummaryRequest();

            super(this.contentSummaryRequest);

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
            this.contentSummaryRequest.setSearchQueryExpr(searchString);
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