module api.content.page {

    export class GetPageTemplatesByCanRenderRequest extends PageTemplateResourceRequest<PageTemplateSummaryListJson, PageTemplateSummary[]> {

        private siteTemplateKey: api.content.site.template.SiteTemplateKey;

        private contentTypeName: api.schema.content.ContentTypeName;

        constructor(siteTemplateKey:api.content.site.template.SiteTemplateKey, contentTypeName:api.schema.content.ContentTypeName) {
            super();
            this.setMethod("GET");
            this.siteTemplateKey = siteTemplateKey;
            this.contentTypeName = contentTypeName;
        }

        getParams():Object {
            return {
                siteTemplateKey: this.siteTemplateKey.toString(),
                contentTypeName: this.contentTypeName.toString()
            }
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "listByCanRender");
        }

        sendAndParse(): wemQ.Promise<PageTemplateSummary[]> {

            return this.send().then((response: api.rest.JsonResponse<PageTemplateSummaryListJson>) => {
                return response.getResult().templates.map((templateJson:PageTemplateSummaryJson) => {
                    return this.fromJsonToPageTemplateSummary(templateJson);
                });
            });
        }
    }
}