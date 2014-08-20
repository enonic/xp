module api.content.page {

    export class GetDefaultPageTemplateRequest extends PageTemplateResourceRequest<PageTemplateJson, PageTemplate> {

        private siteTemplateKey: api.content.site.template.SiteTemplateKey;

        private contentTypeName: api.schema.content.ContentTypeName;

        constructor(siteTemplateKey: api.content.site.template.SiteTemplateKey, contentName: api.schema.content.ContentTypeName) {
            super();
            this.setMethod("GET");
            this.siteTemplateKey = siteTemplateKey;
            this.contentTypeName = contentName;
        }

        getParams(): Object {
            return {
                siteTemplateKey: this.siteTemplateKey.toString(),
                contentTypeName: this.contentTypeName.toString()
            }
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "default");
        }

        sendAndParse(): wemQ.Promise<PageTemplate> {

            return this.send().then((response: api.rest.JsonResponse<PageTemplateJson>) => {

                if (response.hasResult()) {
                    return this.fromJsonToPageTemplate(response.getResult());
                }
                else {
                    return null;
                }
            });
        }
    }
}