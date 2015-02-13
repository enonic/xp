module api.content {

    export class GetContentSummaryByIds {

        private ids: ContentId[];

        constructor(ids: ContentId[]) {
            this.ids = ids;
        }

        get(): wemQ.Promise<api.content.ContentSummary[]> {
            var deferred = wemQ.defer<api.content.ContentSummary[]>();

            var allPromises = (this.ids || []).map((contentId: ContentId) => {
                return new api.content.GetContentSummaryByIdRequest(contentId).sendAndParse();
            });
            var settledPromises: wemQ.Promise<wemQ.PromiseState<api.content.ContentSummary>[]> = wemQ.allSettled(allPromises);

            settledPromises.spread((...contentResults: wemQ.PromiseState<api.content.ContentSummary>[])=> {
                if (!contentResults) {
                    deferred.resolve([]);
                    return;
                }

                var contents: api.content.ContentSummary[] = contentResults.
                    filter(contentResult => contentResult.state === 'fulfilled').
                    map(contentResult => contentResult.value);

                deferred.resolve(contents);
            }).done();

            return deferred.promise;
        }

    }
}