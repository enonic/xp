module api.content {

    export class GetContentSummaryByIds {

        private ids: ContentId[];

        constructor(ids: ContentId[]) {
            this.ids = ids;
        }

        get(): wemQ.Promise<api.content.ContentSummary[]> {

            var allPromises = (this.ids || []).map((contentId: ContentId) => {

                return new api.content.GetContentSummaryByIdRequest(contentId).sendAndParse();
            });

            return wemQ.all(allPromises);
        }

    }
}