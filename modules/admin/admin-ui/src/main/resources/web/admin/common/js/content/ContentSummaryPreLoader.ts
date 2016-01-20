module api.content {

    export class ContentSummaryPreLoader extends api.util.loader.BaseLoader<json.ContentQueryResultJson<json.ContentSummaryJson>, ContentSummary> {

        constructor(request: api.rest.ResourceRequest<json.ContentQueryResultJson<json.ContentSummaryJson>, ContentSummary[]>) {
            super(request);
        }

        preLoad(ids: string): wemQ.Promise<ContentSummary[]> {
            this.notifyLoadingData(false);

            let contentIds = ids.split(";").map((id) => {
                return new api.content.ContentId(id);
            });
            return new GetContentSummaryByIds(contentIds).
            get().
            then((results: ContentSummary[]) => {
                if (this.getComparator()) {
                    this.setResults(results.sort(this.getComparator().compare));
                } else {
                    this.setResults(results);
                }
                this.notifyLoadedData(results);
                return this.getResults();
            });
        }
    }


}