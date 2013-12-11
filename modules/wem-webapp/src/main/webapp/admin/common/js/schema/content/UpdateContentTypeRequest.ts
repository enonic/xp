module api_schema_content {

    export class UpdateContentTypeRequest extends ContentTypeResourceRequest<api_schema_content_json.ContentTypeJson> {

        private contentTypeToUpdate:ContentTypeName;

        private name:ContentTypeName;

        private config:string;

        private icon: api_icon.Icon;

        constructor(contentTypeToUpdate:ContentTypeName, name:ContentTypeName, config:string, icon: api_icon.Icon) {
            super();
            super.setMethod('POST');
            this.contentTypeToUpdate = contentTypeToUpdate;
            this.name = name;
            this.config = config;
            this.icon = icon;
        }

        getParams():Object {
            return {
                contentTypeToUpdate: this.contentTypeToUpdate.toString(),
                name: this.name.toString(),
                config: this.config,
                icon: this.icon != null ? this.icon.toJson() : null
            }
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "update");
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