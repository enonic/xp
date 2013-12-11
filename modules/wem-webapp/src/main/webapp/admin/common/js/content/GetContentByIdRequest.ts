module api_content {

    export class GetContentByIdRequest extends ContentResourceRequest<api_content_json.ContentJson> {

        private id:ContentId;

        private expand:string;

        constructor(id:ContentId) {
            super();
            super.setMethod("GET");
            this.id = id;
        }

        public setExpand(expand:string):GetContentByIdRequest {
            this.expand = expand;
            return this;
        }

        getParams():Object {
            return {
                id: this.id.toString(),
                expand: this.expand
            };
        }

        getRequestPath():api_rest.Path {
            return super.getResourcePath();
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