module api_content_site {

    export class SiteResourceRequest<T> extends api_rest.ResourceRequest<T>{


        private resourcePath:api_rest.Path;

        constructor() {
            super();
            this.resourcePath = api_rest.Path.fromParent(super.getRestPath(), "content", "site");
        }

        getResourcePath():api_rest.Path {
            return this.resourcePath;
        }
    }
}