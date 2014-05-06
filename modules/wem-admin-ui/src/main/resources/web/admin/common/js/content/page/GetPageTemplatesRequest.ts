module api.content.page {

    export class GetPageTemplatesRequest extends PageTemplateResourceRequest<api.content.page.PageTemplateSummaryListJson> {

        private siteTemplateKey: api.content.site.template.SiteTemplateKey;

        constructor(siteTemplateKey: api.content.site.template.SiteTemplateKey) {
            super();
            super.setMethod("GET");
            this.siteTemplateKey = siteTemplateKey;
        }

        getParams(): Object {
            return {
                key: this.siteTemplateKey.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list");
        }

        sendAndParse(): Q.Promise<api.content.page.PageTemplateSummary[]> {

            return this.send().then((response: api.rest.JsonResponse<api.content.page.PageTemplateSummaryListJson>) => {
                return response.getResult().templates.map((templateJson:api.content.page.PageTemplateSummaryJson) => {
                    return this.fromJsonToPageTemplateSummary(templateJson);
                });
            });
        }
    }
}
