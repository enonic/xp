module api.content.site.template {

    export class ExportSiteTemplateRequest extends SiteTemplateResourceRequest<any> {

        private siteTemplateKey: api.content.site.template.SiteTemplateKey;

        constructor(siteTemplateKey:api.content.site.template.SiteTemplateKey) {
            super();
            super.setMethod("GET");
            this.siteTemplateKey = siteTemplateKey;
        }

        getParams():Object {
            return {
                siteTemplateKey: this.siteTemplateKey.toString()
            };
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "export");
        }
    }
}