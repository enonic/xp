module api.content.page {

    export class PageTemplateResourceRequest<T> extends api.rest.ResourceRequest<T>{

        private resourcePath:api.rest.Path;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "content", "page", "template");
        }

        getResourcePath():api.rest.Path {
            return this.resourcePath;
        }

        fromJsonToPageTemplate(json:api.content.page.json.PageTemplateJson):api.content.page.PageTemplate {
            return new PageTemplateBuilder().
                fromJson(json).build();
        }

        fromJsonToPageTemplateSummary(json:api.content.page.json.PageTemplateSummaryJson):api.content.page.PageTemplateSummary {
            return new PageTemplateSummaryBuilder().
                fromJson(json).build();
        }
    }
}