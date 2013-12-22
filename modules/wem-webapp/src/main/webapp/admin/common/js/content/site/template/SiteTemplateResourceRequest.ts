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

        fromJsonArrayToSiteTemplateSummaryArray(jsonArray:api_content_site_template_json.SiteTemplateSummaryJson[]) : api_content_site_template.SiteTemplateSummary[] {

            var summaryArray: api_content_site_template.SiteTemplateSummary[] = [];
            jsonArray.forEach((summaryJson:api_content_site_template_json.SiteTemplateSummaryJson) => {
                summaryArray.push(new api_content_site_template.SiteTemplateSummary(summaryJson));
            });
            return summaryArray;
        }

        fromJsonToSiteTemplate(json:api_content_site_template_json.SiteTemplateJson) : api_content_site_template.SiteTemplate {

            return new api_content_site_template.SiteTemplate(json);
        }
    }
}