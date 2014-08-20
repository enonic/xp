module api.content {

    export class ContentSummaryAndCompareStatusFetcher {

        static fetchChildren(parentContentId: string): wemQ.Promise<ContentSummaryAndCompareStatus[]> {

            var deferred = wemQ.defer<ContentSummaryAndCompareStatus[]>();

            new ListContentByIdRequest(parentContentId).sendAndParse().then((contentSummaries: ContentSummary[])=> {
                CompareContentRequest.fromContentSummaries(contentSummaries).sendAndParse().then((compareResults: CompareContentResults) => {
                    deferred.resolve(ContentSummaryAndCompareStatusFetcher.updateCompareStatus(contentSummaries, compareResults));
                });
            });

            return deferred.promise;
        }

        static fetch(contentId: string): wemQ.Promise<ContentSummaryAndCompareStatus> {

            var deferred = wemQ.defer<ContentSummaryAndCompareStatus>();

            new GetContentByIdRequest(new ContentId(contentId)).sendAndParse().then((content: Content)=> {
                CompareContentRequest.fromContentSummaries([content]).sendAndParse().then((compareResults: CompareContentResults) => {
                    deferred.resolve(ContentSummaryAndCompareStatusFetcher.updateCompareStatus([content], compareResults)[0]);
                });
            });

            return deferred.promise;
        }

        static updateCompareStatus(contentSummaries: ContentSummary[],
                                   compareResults: CompareContentResults): ContentSummaryAndCompareStatus[] {
            var list: ContentSummaryAndCompareStatus[] = [];
            contentSummaries.forEach((contentSummary: ContentSummary) => {
                var compareResult: CompareContentResult = compareResults.get(contentSummary.getId());
                var newEntry = new ContentSummaryAndCompareStatus(contentSummary, compareResult);
                list.push(newEntry)
            });

            return list;
        }
    }
}