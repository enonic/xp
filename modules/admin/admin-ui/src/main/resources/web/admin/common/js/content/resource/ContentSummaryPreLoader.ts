module api.content.resource {

    import ContentSummary = api.content.ContentSummary;
    import PostLoader = api.util.loader.PostLoader;
    import ContentQueryResultJson = api.content.json.ContentQueryResultJson;
    import ContentSummaryJson = api.content.json.ContentSummaryJson;

    export class ContentSummaryPreLoader extends PostLoader<ContentQueryResultJson<ContentSummaryJson>, ContentSummary> {

        protected sendPreLoadRequest(ids: string): Q.Promise<ContentSummary[]> {
            let contentIds = ids.split(";").map((id) => {
                return new api.content.ContentId(id);
            });

            return new GetContentSummaryByIds(contentIds).sendAndParse();
        }
    }

}