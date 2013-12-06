module api_content_page {

    export class PageDescriptorResourceRequest<T> extends api_rest.ResourceRequest<T>{

        private resourcePath:api_rest.Path;

        constructor() {
            super();
            this.resourcePath = api_rest.Path.fromParent(super.getRestPath(), "content", "page", "descriptor");
        }

        getResourcePath():api_rest.Path {
            return this.resourcePath;
        }
    }
}