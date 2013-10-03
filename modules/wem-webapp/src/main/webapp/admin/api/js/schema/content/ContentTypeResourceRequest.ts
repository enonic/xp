module api_schema_content {

    export class ContentTypeResourceRequest extends api_rest.ResourceRequest {

        private resourceUrl:api_rest.Path;

        constructor() {
            super();
            this.resourceUrl = api_rest.Path.fromParent(super.getRestPath(), "schema/content");
        }

        getResourcePath():api_rest.Path {
            return this.resourceUrl;
        }
    }
}