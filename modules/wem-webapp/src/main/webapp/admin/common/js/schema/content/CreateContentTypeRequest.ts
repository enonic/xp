module api_schema_content {

    export class CreateContentTypeRequest extends ContentTypeResourceRequest<api_schema_content_json.ContentTypeJson> {

        private name: string;

        private config: string;

        private icon: api_icon.Icon;

        constructor(name: string, contentType: string, icon: api_icon.Icon) {
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

        getRequestPath(): api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "create");
        }

        sendAndParse(): JQueryPromise<ContentType> {

            var deferred = jQuery.Deferred<ContentType>();

            this.send().done((response: api_rest.JsonResponse<api_schema_content_json.ContentTypeJson>) => {
                deferred.resolve(this.fromJsonToContentType(response.getResult()));
            }).fail((response: api_rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }

}