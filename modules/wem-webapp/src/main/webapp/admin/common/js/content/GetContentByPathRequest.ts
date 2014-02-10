module api.content {

    export class GetContentByPathRequest extends ContentResourceRequest<api.content.json.ContentJson> {

        private contentPath:ContentPath;

        constructor(path:ContentPath) {
            super();
            super.setMethod("GET");
            this.contentPath = path;
        }

        getParams():Object {
            return {
                path: this.contentPath.toString()
            };
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "bypath");
        }

        sendAndParse(): Q.Promise<api.content.Content> {

            var deferred = Q.defer<api.content.Content>();

            this.send().then((response: api.rest.JsonResponse<api.content.json.ContentJson>) => {
                deferred.resolve(this.fromJsonToContent(response.getResult()));
            }).catch((response: api.rest.RequestError) => {
                    deferred.reject(null);
                }).done();

            return deferred.promise;
        }
    }
}