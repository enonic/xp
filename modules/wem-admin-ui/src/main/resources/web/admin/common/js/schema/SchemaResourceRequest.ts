module api.schema {

    export class SchemaResourceRequest<JSON_TYPE, PARSED_TYPE> extends api.rest.ResourceRequest<JSON_TYPE, PARSED_TYPE> {

        private resourcePath: api.rest.Path;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "schema");
        }

        getResourcePath(): api.rest.Path {
            return this.resourcePath;
        }

    }
}