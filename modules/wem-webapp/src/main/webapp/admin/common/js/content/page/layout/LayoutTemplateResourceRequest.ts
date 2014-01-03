module api.content.page.layout {

    export class LayoutTemplateResourceRequest<T> extends api.rest.ResourceRequest<T> {

        private resourcePath:api.rest.Path;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "content", "page", "layout", "template");
        }

        getResourcePath():api.rest.Path {
            return this.resourcePath;
        }

        fromJsonToLayoutTemplate(json: api.content.page.layout.json.LayoutTemplateJson): api.content.page.layout.LayoutTemplate {
            return new LayoutTemplateBuilder().fromJson(json).build();
        }
    }
}