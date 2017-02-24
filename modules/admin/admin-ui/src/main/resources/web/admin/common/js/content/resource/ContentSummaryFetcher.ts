module api.content.resource {

    import CompareContentRequest = api.content.resource.CompareContentRequest;
    import BatchContentRequest = api.content.resource.BatchContentRequest;
    import ContentResponse = api.content.resource.result.ContentResponse;
    import CompareContentResults = api.content.resource.result.CompareContentResults;
    import resolve = Q.resolve;

    export class ContentSummaryFetcher {

        static fetchChildren(parentContentId: ContentId, from: number = 0, size: number = -1,
                             childOrder?: api.content.order.ChildOrder): wemQ.Promise<ContentResponse<ContentSummary>> {

            let deferred = wemQ.defer<ContentResponse<ContentSummary>>();

            new ListContentByIdRequest(parentContentId).setFrom(from).setSize(size).setOrder(childOrder).sendAndParse().then(
                (response: ContentResponse<ContentSummary>)=> {
                    deferred.resolve(response);
                });

            return deferred.promise;
        }

        static fetch(contentId: ContentId): wemQ.Promise<Content> {

            let deferred = wemQ.defer<Content>();

            new GetContentByIdRequest(contentId).sendAndParse().then((content: Content)=> {
                deferred.resolve(content);
            });

            return deferred.promise;
        }

        static fetchByIds(ids: ContentId[]): wemQ.Promise<ContentSummary[]> {

            let deferred = wemQ.defer<ContentSummary[]>();

            if (ids.length > 0) {
                new GetContentSummaryByIds(ids).sendAndParse().then((contentSummaries: ContentSummary[]) => {
                    deferred.resolve(contentSummaries);
                });
            } else {
                deferred.resolve([]);
            }

            return deferred.promise;
        }

        static getReadOnly(contents: ContentSummary[]): wemQ.Promise<string[]> {
            return new IsContentReadOnlyRequest(contents.map(content => content.getContentId())).sendAndParse();
        }
    }
}
