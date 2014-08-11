module api.content.page {

    export class GetPageTemplateByKeyRequest extends PageTemplateResourceRequest<PageTemplateJson, PageTemplate> {

        private siteTemplateKey: api.content.site.template.SiteTemplateKey;

        private pageTemplateKey: PageTemplateKey;

        constructor(pageTemplateKey: PageTemplateKey) {
            super();
            super.setMethod("GET");
            this.pageTemplateKey = pageTemplateKey;
        }

        setSiteTemplateKey(value: api.content.site.template.SiteTemplateKey): GetPageTemplateByKeyRequest {
            this.siteTemplateKey = value;
            return this;
        }

        validate() {
            api.util.assertNotNull(this.siteTemplateKey, "siteTemplateKey cannot be null");
            api.util.assertNotNull(this.pageTemplateKey, "pageTemplateKey cannot be null");
        }

        getParams(): Object {
            return {
                siteTemplateKey: this.siteTemplateKey.toString(),
                key: this.pageTemplateKey.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): Q.Promise<PageTemplate> {

            return this.send().then((response: api.rest.JsonResponse<PageTemplateJson>) => {
                return this.fromJsonToPageTemplate(response.getResult());
            });
        }
    }
}