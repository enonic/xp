module api.content.site.template {

    export class SiteTemplateResourceRequest<T> extends api.rest.ResourceRequest<T> {


        private resourcePath: api.rest.Path;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "content", "site", "template");
        }

        getResourcePath(): api.rest.Path {
            return this.resourcePath;
        }

        fromJsonArrayToSiteTemplateSummaryArray(jsonArray: api.content.site.template.SiteTemplateSummaryJson[]): api.content.site.template.SiteTemplateSummary[] {

            var summaryArray: api.content.site.template.SiteTemplateSummary[] = [];
            jsonArray.forEach((summaryJson: api.content.site.template.SiteTemplateSummaryJson) => {
                summaryArray.push(api.content.site.template.SiteTemplateSummary.fromJson(summaryJson));
            });
            return summaryArray;
        }

        fromJsonToSiteTemplate(json: api.content.site.template.SiteTemplateJson): api.content.site.template.SiteTemplate {

            return api.content.site.template.SiteTemplate.fromJson(json);
        }
    }
}