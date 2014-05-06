module api.schema.content {

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

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "config");
        }
    }
}