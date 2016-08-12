module api.content.resource {

    import QueryExpr = api.query.expr.QueryExpr;

    export class ContentSummaryLoader extends ContentSummaryPreLoader {

        private contentSummaryRequest: ContentSummaryRequest;

        constructor() {
            this.contentSummaryRequest = this.initContentSummaryRequest();

            super(this.contentSummaryRequest);

            this.setSearchQueryExpr();
        }

        protected initContentSummaryRequest(): ContentSummaryRequest {
            return new ContentSummaryRequest();
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

        setContentPath(path: ContentPath) {
            this.contentSummaryRequest.setContentPath(path);
        }

        private setSearchQueryExpr(searchString: string = "") {
            this.contentSummaryRequest.setSearchString(searchString);
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