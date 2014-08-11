module api.content.site.template {

    export class GetAllSiteTemplatesRequest extends SiteTemplateResourceRequest<SiteTemplateSummaryListJson, SiteTemplateSummary[]> {

        constructor() {
            super();
            super.setMethod("GET");
        }

        getParams():Object {
            return {};
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list");
        }

        sendAndParse(): Q.Promise<SiteTemplateSummary[]> {

            return this.send().then((response: api.rest.JsonResponse<SiteTemplateSummaryListJson>) => {
                return this.fromJsonArrayToSiteTemplateSummaryArray(response.getResult().siteTemplates);
            });
        }
    }
}