module api.content.site.template {

    export class GetSiteTemplateRequest extends SiteTemplateResourceRequest<api.content.site.template.SiteTemplateJson> {

        private key: SiteTemplateKey;

        constructor(key: SiteTemplateKey) {
            super();
            super.setMethod("GET");
            this.key = key;
        }

        getParams(): Object {
            return {
                siteTemplateKey: this.key.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath());
        }

        sendAndParse(): Q.Promise<api.content.site.template.SiteTemplate> {

            return this.send().then((response: api.rest.JsonResponse<api.content.site.template.SiteTemplateJson>) => {
                return this.fromJsonToSiteTemplate(response.getResult());
            });
        }
    }
}