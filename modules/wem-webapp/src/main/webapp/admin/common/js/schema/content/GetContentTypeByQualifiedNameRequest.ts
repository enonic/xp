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
                qualifiedName: this.name.toString(),
                mixinReferencesToFormItems: this.mixinReferencesToFormItems
            };
        }

        getRequestPath():api_rest.Path {
            return super.getResourcePath();
        }
    }
}