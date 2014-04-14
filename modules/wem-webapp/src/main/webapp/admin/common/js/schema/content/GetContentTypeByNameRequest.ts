module api.schema.content {

    export class GetContentTypeByNameRequest extends ContentTypeResourceRequest<api.schema.content.json.ContentTypeJson> {

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

        getRequestPath():api.rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): Q.Promise<api.schema.content.ContentType> {

            return this.send().then((response: api.rest.JsonResponse<api.schema.content.json.ContentTypeJson>) => {
                return this.fromJsonToContentType(response.getResult());
            });
        }
    }
}