module api.content {

    export class GetContentSummaryByIds {

        private ids: ContentId[];

        constructor(ids: ContentId[]) {
            this.ids = ids;
        }

        get(): Q.Promise<api.content.ContentSummary[]> {

            var allPromises = (this.ids || []).map((contentId: ContentId) => {

                return new api.content.GetContentByIdRequest(contentId)
                    .setExpand(api.content.ContentResourceRequest.EXPAND_SUMMARY)
                    .sendAndParse();
            });

            return Q.all(allPromises);
        }

    }
}