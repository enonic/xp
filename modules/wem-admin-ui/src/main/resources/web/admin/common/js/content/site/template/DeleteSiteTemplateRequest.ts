module api.content.site.template {

    export class DeleteSiteTemplateRequest extends SiteTemplateResourceRequest<SiteTemplateKey, any> {

        private siteTemplateKey: SiteTemplateKey;

        constructor(siteTemplateId: SiteTemplateKey) {
            super();
            super.setMethod("POST");
            this.siteTemplateKey = siteTemplateId;
        }

        getParams(): Object {
            return {
                siteTemplateKey: this.siteTemplateKey.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "delete");
        }
    }
}