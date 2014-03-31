module api.content {

    export class GetContentSummaryByIds {

        private ids: ContentId[];

        constructor(ids: ContentId[]) {
            this.ids = ids;
        }

        get(): Q.Promise<api.content.ContentSummary[]> {

            var deferred = Q.defer<api.content.ContentSummary[]>();
            var contents: api.content.ContentSummary[] = [];
            var allPromises: Q.Promise<any>[] = [];

            if (!this.ids || this.ids.length == 0) {
                deferred.resolve(contents);
            }
            else {
                this.ids.forEach((contentId: ContentId) => {

                    var promise = new api.content.GetContentByIdRequest(contentId)
                        .setExpand(api.content.ContentResourceRequest.EXPAND_SUMMARY)
                        .sendAndParse();

                    allPromises.push(promise);
                });

                Q.allSettled(allPromises).then((results: Q.PromiseState<api.content.ContentSummary>[])=> {

                    results.forEach((result: Q.PromiseState<api.content.ContentSummary>) => {
                        if (result.state == "fulfilled") {
                            contents.push(result.value);
                        }
                        else if (result.state == "rejected") {
                            deferred.reject(result.reason);
                        }
                    });
                    deferred.resolve(contents);
                });
            }


            return deferred.promise;
        }

    }
}