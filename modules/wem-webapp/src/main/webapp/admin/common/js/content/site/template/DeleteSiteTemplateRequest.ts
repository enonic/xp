module api_content_site_template {

    export class DeleteSiteTemplateRequest extends SiteTemplateResourceRequest<SiteTemplateKey> {

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

        getRequestPath(): api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "delete");
        }
    }
}