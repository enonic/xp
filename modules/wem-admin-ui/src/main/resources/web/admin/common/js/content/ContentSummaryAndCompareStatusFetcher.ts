module api.content {

    export class ContentSummaryAndCompareStatusFetcher {

        private parentId: string;

        constructor(parentId: string) {
            this.parentId = parentId;
        }

        getParams(): Object {
            return {
                parentId: this.parentId
            };
        }


        fetch(parentContentId: string): Q.Promise<ContentSummaryAndCompareStatus[]> {

            var deferred = Q.defer<ContentSummaryAndCompareStatus[]>();

            new ListContentByIdRequest(parentContentId).sendAndParse().then((contentSummaries: ContentSummary[])=> {
                CompareContentRequest.fromContentSummaries(contentSummaries).sendAndParse().then((compareResults: CompareContentResults) => {
                    var list: ContentSummaryAndCompareStatus[] = [];
                    contentSummaries.forEach((contentSummary: ContentSummary) => {
                            var compareResult: CompareContentResult = compareResults.get(contentSummary.getId());
                            var newEntry = new ContentSummaryAndCompareStatus(contentSummary, compareResult);
                            list.push(newEntry)
                        }
                    );
                    deferred.resolve(list);
                });
            });
            return deferred.promise;
        }
    }
}