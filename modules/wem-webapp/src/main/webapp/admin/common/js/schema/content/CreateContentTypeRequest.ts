module api.schema.content {

    export class CreateContentTypeRequest extends ContentTypeResourceRequest<api.schema.content.json.ContentTypeJson> {

        private name: string;

        private config: string;

        private icon: api.icon.Icon;

        constructor(name: string, contentType: string, icon: api.icon.Icon) {
            super();
            super.setMethod('POST');
            this.name = name;
            this.config = contentType;
            this.icon = icon;
        }

        getParams(): Object {
            return {
                name: this.name,
                config: this.config,
                icon: this.icon != null ? this.icon.toJson() : null
            }
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "create");
        }

        sendAndParse(): Q.Promise<ContentType> {

            var deferred = Q.defer<ContentType>();

            this.send().done((response: api.rest.JsonResponse<api.schema.content.json.ContentTypeJson>) => {
                deferred.resolve(this.fromJsonToContentType(response.getResult()));
            }).fail((response: api.rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred.promise;
        }
    }

}