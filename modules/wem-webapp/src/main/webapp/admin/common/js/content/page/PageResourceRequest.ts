module api.content.page {

    export class PageResourceRequest<T> extends api.rest.ResourceRequest<T>{

        private resourcePath:api.rest.Path;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "content", "page");
        }

        getResourcePath():api.rest.Path {
            return this.resourcePath;
        }
    }
}