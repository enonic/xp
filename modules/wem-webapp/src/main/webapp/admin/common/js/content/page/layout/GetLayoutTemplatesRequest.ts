module api.content.page.layout {

    export class GetLayoutTemplatesRequest extends LayoutTemplateResourceRequest<api.content.page.layout.json.LayoutTemplateSummaryListJson> {

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