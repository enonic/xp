module api.content.site.template {

    export class GetSiteTemplateRequest extends SiteTemplateResourceRequest<SiteTemplateJson, SiteTemplate> {

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

        sendAndParse(): wemQ.Promise<SiteTemplate> {

            return this.send().then((response: api.rest.JsonResponse<SiteTemplateJson>) => {
                return this.fromJsonToSiteTemplate(response.getResult());
            });
        }
    }
}