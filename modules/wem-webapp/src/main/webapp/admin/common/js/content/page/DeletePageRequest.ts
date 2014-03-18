module api.content.page {

    export class DeletePageRequest extends PageResourceRequest<api.content.json.ContentJson> implements PageCUDRequest {

        private contentId: api.content.ContentId;

        constructor(contentId: api.content.ContentId) {
            super();
            super.setMethod("GET");
            this.contentId = contentId;
        }

        getParams(): Object {
            return {
                contentId: this.contentId.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "delete");
        }

        sendAndParse(): Q.Promise<api.content.Content> {

            var deferred = Q.defer<api.content.Content>();

            this.send().
                then((response: api.rest.JsonResponse<api.content.json.ContentJson>) => {
                    var content = null;
                    if (!response.isBlank()) {
                        content = this.fromJsonToContent(response.getResult());
                    }
                    deferred.resolve(content);
                }).catch((response: api.rest.RequestError) => {
                    deferred.reject(null);
                }).done();

            return deferred.promise;
        }
    }
}