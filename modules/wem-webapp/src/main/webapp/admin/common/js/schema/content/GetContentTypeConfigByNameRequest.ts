module api_schema_content {

    export class GetContentTypeConfigByNameRequest extends ContentTypeResourceRequest<GetContentTypeConfigResult> {

        private name:ContentTypeName;

        constructor(name:ContentTypeName) {
            super();
            super.setMethod("GET");
            this.name = name;
        }

        getParams():Object {
            return {
                name: this.name.toString()
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "config");
        }
    }
}