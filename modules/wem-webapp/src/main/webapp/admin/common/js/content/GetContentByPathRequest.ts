module api_content {

    export class GetContentByPathRequest extends ContentResourceRequest<api_content_json.ContentJson> {

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

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "bypath");
        }

        sendAndParse(): JQueryPromise<api_content.Content> {

            var deferred = jQuery.Deferred<api_content.Content>();

            this.send().done((response: api_rest.JsonResponse<api_content_json.ContentJson>) => {
                deferred.resolve(this.fromJsonToContent(response.getResult()));
            }).fail((response: api_rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}