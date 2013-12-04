module api_content_page_layout {

    export class LayoutTemplateResourceRequest extends api_rest.ResourceRequest<any> {

        private resourcePath:api_rest.Path;

        constructor() {
            super();
            this.resourcePath = api_rest.Path.fromParent(super.getRestPath(), "content/page/layout/template");
        }

        getResourcePath():api_rest.Path {
            return this.resourcePath;
        }
    }
}