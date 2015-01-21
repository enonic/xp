module api.content {

    export class ContentSummaryLoader extends api.util.loader.BaseLoader<json.ContentQueryResultJson<json.ContentSummaryJson>, ContentSummary> {

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

            if (this.isLoading()) {
                var onLoaded = () => {
                    this.search(searchString);
                    this.unLoadedData(onLoaded);
                };
                this.onLoadedData(onLoaded);
                return;
            }

            this.contentSummaryRequest.setQueryExpr(searchString);

            this.load();
        }

        sendRequest(): wemQ.Promise<ContentSummary[]> {
            return this.contentSummaryRequest.sendAndParse();
        }

    }


}