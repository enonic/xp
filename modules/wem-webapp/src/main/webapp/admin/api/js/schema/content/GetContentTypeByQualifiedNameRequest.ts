module api_schema_content {

    export class GetContentTypeByQualifiedNameRequest extends ContentTypeResourceRequest<api_schema_content_json.ContentTypeJson> {

        private qualifiedName:string;

        private mixinReferencesToFormItems:boolean = true;

        constructor(qualifiedName:string) {
            super();
            super.setMethod("GET");
            this.qualifiedName = qualifiedName;
        }

        setMixinReferencesToFormItems(value:boolean):GetContentTypeByQualifiedNameRequest {
            this.mixinReferencesToFormItems = value;
            return this;
        }

        getParams():Object {
            return {
                qualifiedName: this.qualifiedName,
                mixinReferencesToFormItems: this.mixinReferencesToFormItems
            };
        }

        getRequestPath():api_rest.Path {
            return super.getResourcePath();
        }
    }
}