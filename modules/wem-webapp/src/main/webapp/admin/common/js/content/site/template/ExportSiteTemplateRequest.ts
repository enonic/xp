module api_content_site_template {

    export class ExportSiteTemplateRequest extends SiteTemplateResourceRequest<any> {

        private siteTemplateKey: api_content_site_template.SiteTemplateKey;

        constructor(siteTemplateKey:api_content_site_template.SiteTemplateKey) {
            super();
            super.setMethod("GET");
            this.siteTemplateKey = siteTemplateKey;
        }

        getParams():Object {
            return {
                siteTemplateKey: this.siteTemplateKey.toString()
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "export");
        }
    }
}