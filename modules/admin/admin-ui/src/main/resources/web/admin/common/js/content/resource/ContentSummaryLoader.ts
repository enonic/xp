module api.content.resource {

    import QueryExpr = api.query.expr.QueryExpr;

    export class ContentSummaryLoader extends ContentSummaryPreLoader {

        protected request: ContentSummaryRequest;

        constructor() {
            super();

            this.setSearchQueryExpr();
        }

        protected createRequest(): ContentSummaryRequest {
            return new ContentSummaryRequest();
        }

        protected getRequest(): ContentSummaryRequest {
            return this.request;
        }

        setAllowedContentTypes(contentTypes: string[]) {
            this.getRequest().setAllowedContentTypes(contentTypes);
        }

        setAllowedContentTypeNames(contentTypeNames: api.schema.content.ContentTypeName[]) {
            this.getRequest().setAllowedContentTypeNames(contentTypeNames);
        }

        setSize(size: number) {
            this.getRequest().setSize(size);
        }

        setContentPath(path: ContentPath) {
            this.getRequest().setContentPath(path);
        }

        isPartiallyLoaded(): boolean {
            return this.getRequest().isPartiallyLoaded();
        }

        private setSearchQueryExpr(searchString: string = "") {
            this.getRequest().setSearchString(searchString);
        }

        resetParams() {
            this.getRequest().resetParams()
        }

        search(searchString: string): wemQ.Promise<ContentSummary[]> {
            this.setSearchQueryExpr(searchString);

            return this.load();
        }

    }


}