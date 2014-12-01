module api.content {

    export class ContentSummaryAndCompareStatusFetcher {

        static fetchChildren(parentContentId: ContentId, from: number = 0, size: number = -1,
                             childOrder?: ChildOrder): wemQ.Promise<ContentResponse<ContentSummaryAndCompareStatus>> {

            var deferred = wemQ.defer<ContentResponse<ContentSummaryAndCompareStatus>>();

            new ListContentByIdRequest(parentContentId).
                setFrom(from).
                setSize(size).
                setOrder(childOrder).
                sendAndParse().
                then((response: ContentResponse<ContentSummary>)=> {
                    CompareContentRequest.fromContentSummaries(response.getContents()).sendAndParse().then((compareResults: CompareContentResults) => {
                        var result = new ContentResponse<ContentSummaryAndCompareStatus>(
                            ContentSummaryAndCompareStatusFetcher.updateCompareStatus(response.getContents(), compareResults),
                            response.getMetadata()
                        );
                        deferred.resolve(result);
                    });
                });

            return deferred.promise;
        }

        static fetch(contentId: ContentId): wemQ.Promise<ContentSummaryAndCompareStatus> {

            var deferred = wemQ.defer<ContentSummaryAndCompareStatus>();

            new GetContentByIdRequest(contentId).sendAndParse().then((content: Content)=> {
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