module api.content.site.template {

    export class SiteTemplateResourceRequest<JSON_TYPE, PARSED_TYPE> extends api.rest.ResourceRequest<JSON_TYPE, PARSED_TYPE> {


        private resourcePath: api.rest.Path;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "content", "site", "template");
        }

        getResourcePath(): api.rest.Path {
            return this.resourcePath;
        }

        fromJsonArrayToSiteTemplateSummaryArray(jsonArray: SiteTemplateSummaryJson[]): SiteTemplateSummary[] {

            var summaryArray: SiteTemplateSummary[] = [];
            jsonArray.forEach((summaryJson: SiteTemplateSummaryJson) => {
                summaryArray.push(SiteTemplateSummary.fromJson(summaryJson));
            });
            return summaryArray;
        }

        fromJsonToSiteTemplate(json: SiteTemplateJson): SiteTemplate {

            return SiteTemplate.fromJson(json);
        }
    }
}