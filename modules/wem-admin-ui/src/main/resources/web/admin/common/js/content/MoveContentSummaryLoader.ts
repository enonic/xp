module api.content {

    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LoadingDataEvent = api.util.loader.event.LoadingDataEvent;
    import QueryField = api.query.QueryField;

    export class MoveContentSummaryLoader extends api.util.loader.BaseLoader<json.ContentQueryResultJson<json.ContentSummaryJson>, ContentSummary> {

        private preservedSearchString: string;

        private contentSummaryRequest: ContentSummaryRequest;

        private filterContentPath: ContentPath;

        private contentByPathComparator;

        constructor() {
            this.contentByPathComparator = new ContentByPathComparator()
            this.contentSummaryRequest = new ContentSummaryRequest();
            super(this.contentSummaryRequest);
        }

        setSize(size: number) {
            this.contentSummaryRequest.setSize(size);
        }

        setFilterContentPath(filterContentPath: ContentPath) {
            this.filterContentPath = filterContentPath;
        }

        search(searchString: string) {

            if (this.isLoading()) {
                this.preservedSearchString = searchString;
                return;
            }

            this.contentSummaryRequest.setQueryExpr(searchString);

            this.load();
        }


        load() {

            this.notifyLoadingData();

            this.sendRequest().done((contents: ContentSummary[]) => {

                contents = this.filterContent(contents);
                if (contents && contents.length > 0) {
                    contents.sort(this.contentByPathComparator.compare);
                    this.notifyLoadedData(contents);
                }
                if (this.preservedSearchString) {
                    this.search(this.preservedSearchString);
                    this.preservedSearchString = null;
                }

            });
        }

        private filterContent(contents: ContentSummary[]): ContentSummary[] {
            if (!!this.filterContentPath) {
                return contents.filter((content: ContentSummary) => (
                                                                        !content.getPath().isDescendantOf(this.filterContentPath)) &&
                                                                    !this.filterContentPath.isChildOf(content.getPath()) &&
                                                                    !this.filterContentPath.equals(content.getPath())
                );
            }
            return [];
        }

        sendRequest(): wemQ.Promise<ContentSummary[]> {
            return this.contentSummaryRequest.sendAndParse();
        }

    }

}
