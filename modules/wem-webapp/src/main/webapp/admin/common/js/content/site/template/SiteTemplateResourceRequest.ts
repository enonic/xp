module api_content_site_template {

    export class SiteTemplateResourceRequest<T> extends api_rest.ResourceRequest<T>{


        private resourcePath:api_rest.Path;

        constructor() {
            super();
            this.resourcePath = api_rest.Path.fromParent(super.getRestPath(), "content", "site", "template");
        }

        getResourcePath():api_rest.Path {
            return this.resourcePath;
        }
    }
}