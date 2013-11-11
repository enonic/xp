module api_schema {

    export class SchemaResourceRequest extends api_rest.ResourceRequest<any> {

        private resourcePath:api_rest.Path;

        constructor() {
            super();
            this.resourcePath = api_rest.Path.fromParent(super.getRestPath(), "schema");
        }

        getResourcePath():api_rest.Path {
            return this.resourcePath;
        }
    }
}