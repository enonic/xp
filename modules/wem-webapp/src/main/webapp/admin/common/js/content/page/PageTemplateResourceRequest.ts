module api_content_page {

    export class PageTemplateResourceRequest<T> extends api_rest.ResourceRequest<T>{

        private resourcePath:api_rest.Path;

        constructor() {
            super();
            this.resourcePath = api_rest.Path.fromParent(super.getRestPath(), "content", "page", "template");
        }

        getResourcePath():api_rest.Path {
            return this.resourcePath;
        }

        fromJsonToPageTemplate(json:api_content_page_json.PageTemplateJson):api_content_page.PageTemplate {
            return new api_content_page.PageTemplateBuilder().
                fromJson(json).build();
        }
    }
}