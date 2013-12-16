module api_schema_content {

    export class GetContentTypeByNameRequest extends ContentTypeResourceRequest<api_schema_content_json.ContentTypeJson> {

        private name:ContentTypeName;

        private mixinReferencesToFormItems:boolean = true;

        constructor(name:ContentTypeName) {
            super();
            super.setMethod("GET");
            this.name = name;
        }

        setMixinReferencesToFormItems(value:boolean):GetContentTypeByNameRequest {
            this.mixinReferencesToFormItems = value;
            return this;
        }

        getParams():Object {
            return {
                name: this.name.toString(),
                mixinReferencesToFormItems: this.mixinReferencesToFormItems
            };
        }

        getRequestPath():api_rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): JQueryPromise<api_schema_content.ContentType> {

            var deferred = jQuery.Deferred<api_schema_content.ContentType>();

            this.send().done((response: api_rest.JsonResponse<api_schema_content_json.ContentTypeJson>) => {
                deferred.resolve(this.fromJsonToContentType(response.getResult()));
            }).fail((response: api_rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}