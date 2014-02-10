module api.content {

    export class GetContentByIdRequest extends ContentResourceRequest<api.content.json.ContentJson> {

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

        getRequestPath():api.rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): Q.Promise<api.content.Content> {

            var deferred = Q.defer<api.content.Content>();

            this.send().done((response: api.rest.JsonResponse<api.content.json.ContentJson>) => {
                deferred.resolve(this.fromJsonToContent(response.getResult()));
            }).fail((response: api.rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred.promise;
        }
    }
}