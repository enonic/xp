module api.content.resource {

    import BatchContentResult = api.content.resource.result.BatchContentResult;

    export class GetContentSummaryByIds extends ContentResourceRequest<BatchContentResult<api.content.json.ContentJson>, Content[]> {

        private ids: ContentId[];

        constructor(ids: ContentId[]) {
            super();
            super.setMethod("POST");
            this.ids = ids;
        }

        getParams(): Object {
            return {
                contentIds: this.ids.map(id => id.toString())
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'resolveByIds');
        }

        sendAndParse(): wemQ.Promise<Content[]> {
            if (this.ids && this.ids.length > 0) {
                return this.send().then((response: api.rest.JsonResponse<BatchContentResult<api.content.json.ContentJson>>) => {
                    return Content.fromJsonArray(response.getResult().contents);
                });
            } else {
                var deferred = wemQ.defer<Content[]>();
                deferred.resolve([]);
                return deferred.promise;
            }
        }

    }
}