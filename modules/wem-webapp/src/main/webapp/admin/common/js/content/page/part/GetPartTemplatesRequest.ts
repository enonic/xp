module api_content_page_part {

    export class GetPartTemplatesRequest extends PartTemplateResourceRequest {

        private siteTemplateKey:api_content_site_template.SiteTemplateKey;

        constructor(siteTemplateKey:api_content_site_template.SiteTemplateKey) {
            super();
            super.setMethod("POST");
            this.siteTemplateKey = siteTemplateKey;
        }

        setSiteTemplateKey(siteTemplateKey:api_content_site_template.SiteTemplateKey):GetPartTemplatesRequest {
            this.siteTemplateKey = siteTemplateKey;
            return this;
        }

        getParams():Object {
            return {
                siteTemplateKey: this.siteTemplateKey.toString()
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "list");
        }
    }
}