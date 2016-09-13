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

        isPartiallyLoaded(): boolean {
            return this.contentSummaryRequest.isPartiallyLoaded();
        }

        private setSearchQueryExpr(searchString: string = "") {
            this.contentSummaryRequest.setSearchString(searchString);
        }

        resetParams() {
            this.contentSummaryRequest.resetParams()
        }

        search(searchString: string): wemQ.Promise<ContentSummary[]> {
            this.setSearchQueryExpr(searchString);

            return this.load();
        }

    }


}