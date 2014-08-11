module api.content {

    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LoadingDataEvent = api.util.loader.event.LoadingDataEvent;

    export class ContentSummaryLoader extends api.util.loader.BaseLoader<json.ContentQueryResultJson<json.ContentSummaryJson>, ContentSummary> {

        private preservedSearchString: string;

        private contentSummaryRequest: ContentSummaryRequest;

        constructor() {
            this.contentSummaryRequest = new ContentSummaryRequest();
            super(this.contentSummaryRequest);
        }

        setAllowedContentTypes(contentTypes: string[]) {
            this.contentSummaryRequest.setAllowedContentTypes(contentTypes);
        }

        setSize(size: number) {
            this.contentSummaryRequest.setSize(size);
        }

        search(searchString: string) {

            if (this.loading()) {
                this.preservedSearchString = searchString;
                return;
            }

            this.contentSummaryRequest.setQueryString(searchString);

            this.load();
        }


        load() {

            this.loading(true);
            this.notifyLoadingData();

            this.sendRequest().done((contents: ContentSummary[]) => {

                this.loading(false);
                this.notifyLoadedData(contents);
                if (this.preservedSearchString) {
                    this.search(this.preservedSearchString);
                    this.preservedSearchString = null;
                }

            });
        }

        sendRequest(): Q.Promise<ContentSummary[]> {
            return this.contentSummaryRequest.sendAndParse();
        }

    }

    class ContentSummaryRequest extends api.rest.ResourceRequest<json.ContentQueryResultJson<json.ContentSummaryJson>, ContentSummary[]> {

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

        send(): Q.Promise<api.rest.JsonResponse<json.ContentQueryResultJson<json.ContentSummaryJson>>> {
            return this.request.send();
        }

        sendAndParse(): Q.Promise<ContentSummary[]> {

            return this.request.sendAndParse().
                then((queryResult: ContentQueryResult<ContentSummary,json.ContentSummaryJson>) => {
                    return queryResult.getContents();
                });
        }

        setAllowedContentTypes(contentTypes: string[]) {
            this.contentQuery.setContentTypeNames(this.createContentTypeNames(contentTypes));
        }

        setSize(size: number) {
            this.contentQuery.setSize(size);
        }

        setQueryString(searchString: string) {
            var fulltextExpression: api.query.expr.Expression = api.query.FulltextSearchExpressionFactory.create(searchString);
            var queryExpr: api.query.expr.QueryExpr = new api.query.expr.QueryExpr(fulltextExpression);
            this.contentQuery.setQueryExpr(queryExpr);
        }

        private createContentTypeNames(names: string[]): api.schema.content.ContentTypeName[] {
            return (names || []).map((name: string) => new api.schema.content.ContentTypeName(name));
        }
    }

}
