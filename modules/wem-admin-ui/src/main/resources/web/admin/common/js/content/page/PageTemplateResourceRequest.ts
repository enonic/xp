module api.content.page {

    export class PageTemplateResourceRequest<JSON_TYPE, PARSED_TYPE> extends api.rest.ResourceRequest<JSON_TYPE, PARSED_TYPE>{

        private resourcePath:api.rest.Path;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "content", "page", "template");
        }

        getResourcePath():api.rest.Path {
            return this.resourcePath;
        }

        fromJsonToPageTemplate(json:api.content.page.PageTemplateJson):api.content.page.PageTemplate {
            return new PageTemplateBuilder().
                fromJson(json).build();
        }

        fromJsonToPageTemplateSummary(json:api.content.page.PageTemplateSummaryJson):api.content.page.PageTemplateSummary {
            return new PageTemplateSummaryBuilder().
                fromJson(json).build();
        }
    }
}