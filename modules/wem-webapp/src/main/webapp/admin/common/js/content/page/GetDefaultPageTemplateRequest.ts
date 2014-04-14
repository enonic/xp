module api.content.page {

    export class GetDefaultPageTemplateRequest extends PageTemplateResourceRequest<api.content.page.PageTemplateJson> {

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

        sendAndParse(): Q.Promise<api.content.page.PageTemplate> {

            return this.send().then((response: api.rest.JsonResponse<api.content.page.PageTemplateJson>) => {

                    return this.fromJsonToPageTemplate(response.getResult());
                    
                });
        }
    }
}