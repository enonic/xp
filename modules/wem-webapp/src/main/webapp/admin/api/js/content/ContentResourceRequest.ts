module api_content {

    export class ContentResourceRequest extends api_rest.ResourceRequest{

        private resourcePath:api_rest.Path;

        constructor() {
            super();
            this.resourcePath = api_rest.Path.fromParent(super.getRestPath(), "content");
        }

        getResourcePath():api_rest.Path {
            return this.resourcePath;
        }
    }
}