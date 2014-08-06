module api.schema {

    export class SchemaResourceRequest<T> extends api.rest.ResourceRequest<T> {

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