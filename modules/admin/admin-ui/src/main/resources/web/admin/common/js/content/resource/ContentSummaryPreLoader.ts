module api.content.resource {

    export class ContentSummaryPreLoader extends api.util.loader.PostLoader<json.ContentQueryResultJson<json.ContentSummaryJson>, ContentSummary> {

        constructor(request: api.rest.ResourceRequest<json.ContentQueryResultJson<json.ContentSummaryJson>, ContentSummary[]>) {
            super(request);
        }

        protected sendPreLoadRequest(ids: string): Q.Promise<api.content.ContentSummary[]> {
            let contentIds = ids.split(";").map((id) => {
                return new api.content.ContentId(id);
            });

            return new GetContentSummaryByIds(contentIds).sendAndParse();
        }
    }


}