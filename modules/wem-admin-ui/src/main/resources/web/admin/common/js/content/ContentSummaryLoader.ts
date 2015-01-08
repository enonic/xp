module api.content {

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

            this.contentSummaryRequest.setQueryExpr(searchString);

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

        sendRequest(): wemQ.Promise<ContentSummary[]> {
            return this.contentSummaryRequest.sendAndParse();
        }

    }


}
