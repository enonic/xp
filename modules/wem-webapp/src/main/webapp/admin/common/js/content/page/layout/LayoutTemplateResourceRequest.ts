module api_content_page_layout {

    export class LayoutTemplateResourceRequest<T> extends api_rest.ResourceRequest<T> {

        private resourcePath:api_rest.Path;

        constructor() {
            super();
            this.resourcePath = api_rest.Path.fromParent(super.getRestPath(), "content", "page", "layout", "template");
        }

        getResourcePath():api_rest.Path {
            return this.resourcePath;
        }

        fromJsonToLayoutTemplate(json: api_content_page_layout_json.LayoutTemplateJson): api_content_page_layout.LayoutTemplate {
            return new LayoutTemplateBuilder().fromJson(json).build();
        }
    }
}