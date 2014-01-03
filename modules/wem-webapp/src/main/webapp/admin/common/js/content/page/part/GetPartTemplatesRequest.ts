module api.content.page.part {

    export class GetPartTemplatesRequest extends PartTemplateResourceRequest<api.content.page.part.json.PartTemplateSummaryListJson> {

        private siteTemplateKey:api.content.site.template.SiteTemplateKey;

        constructor(siteTemplateKey:api.content.site.template.SiteTemplateKey) {
            super();
            super.setMethod("GET");
            this.siteTemplateKey = siteTemplateKey;
        }

        getParams():Object {
            return {
                key: this.siteTemplateKey.toString()
            };
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list");
        }
    }
}