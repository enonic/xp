module api.content.page {

    export class GetPageTemplatesRequest extends PageTemplateResourceRequest<PageTemplateSummaryListJson, PageTemplateSummary[]> {

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

        sendAndParse(): Q.Promise<PageTemplateSummary[]> {

            return this.send().then((response: api.rest.JsonResponse<PageTemplateSummaryListJson>) => {
                return response.getResult().templates.map((templateJson:PageTemplateSummaryJson) => {
                    return this.fromJsonToPageTemplateSummary(templateJson);
                });
            });
        }
    }
}
