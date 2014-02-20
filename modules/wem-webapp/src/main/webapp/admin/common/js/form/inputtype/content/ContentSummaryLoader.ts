module api.form.inputtype.content {

    export class ContentSummaryLoader implements api.util.Loader {

        private isLoading: boolean;

        private preservedSearchString: string;

        private listeners: ContentSummaryLoaderListener[] = [];

        private loaderHelper: api.util.LoaderHelper;

        private contentQuery: api.content.query.ContentQuery;

        constructor(delay: number = 500) {
            this.isLoading = false;
            this.contentQuery = new api.content.query.ContentQuery();
            this.loaderHelper = new api.util.LoaderHelper(this.doRequest, this, delay);
        }

        setAllowedContentTypes(contentTypes: string[]) {
            this.contentQuery.setContentTypeNames(this.createContentTypeNames(contentTypes));
        }

        setSize(size: number) {
            this.contentQuery.setSize(size);
        }

        search(searchString: string) {
            if (this.isLoading) {
                this.preservedSearchString = searchString;
                return;
            }

            this.loaderHelper.search(searchString);
        }

        private doRequest(searchString: string) {
            this.isLoading = true;
            this.notifyLoading();


            var fulltextExpression: api.query.expr.Expression = api.query.FulltextSearchExpressionFactory.create(searchString);
            var queryExpr: api.query.expr.QueryExpr = new api.query.expr.QueryExpr(fulltextExpression);
            this.contentQuery.setQueryExpr(queryExpr)


            new api.content.ContentQueryRequest<api.content.json.ContentSummaryJson,api.content.ContentSummary>(this.contentQuery).
                setExpand(api.rest.Expand.SUMMARY).
                send().done((jsonResponse: api.rest.JsonResponse<api.content.json.ContentQueryResultJson<api.content.json.ContentSummaryJson>>) => {

                    var result: api.content.json.ContentQueryResultJson<api.content.json.ContentSummaryJson> = jsonResponse.getResult();
                    this.isLoading = false;
                    this.notifyLoaded(api.content.ContentSummary.fromJsonArray(result.contents));
                    if (this.preservedSearchString) {
                        this.search(this.preservedSearchString);
                        this.preservedSearchString = null;
                    }

                });
        }

        addListener(listener: ContentSummaryLoaderListener) {
            this.listeners.push(listener);
        }

        removeListener(listenerToRemove: ContentSummaryLoaderListener) {
            this.listeners = this.listeners.filter((listener) => {
                return listener != listenerToRemove;
            })
        }

        private createContentTypeNames(names: string[]): api.schema.content.ContentTypeName[] {

            var contentTypeNames: api.schema.content.ContentTypeName[] = [];

            if (names == null) {
                return contentTypeNames;
            }

            names.forEach((name: string) => {
                contentTypeNames.push(new api.schema.content.ContentTypeName(name));
            });

            return contentTypeNames;
        }

        private notifyLoading() {
            this.listeners.forEach((listener: ContentSummaryLoaderListener) => {
                if (listener.onLoading) {
                    listener.onLoading();
                }
            });
        }

        private notifyLoaded(contentSummaries: api.content.ContentSummary[]) {
            this.listeners.forEach((listener: ContentSummaryLoaderListener) => {
                if (listener.onLoaded) {
                    listener.onLoaded(contentSummaries);
                }
            });
        }

    }

}