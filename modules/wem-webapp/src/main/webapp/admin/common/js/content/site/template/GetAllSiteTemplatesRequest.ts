module api_content_site_template {

    export class GetAllSiteTemplatesRequest extends SiteTemplateResourceRequest<api_content_site_template_json.SiteTemplateSummaryListJson> {

        constructor() {
            super();
            super.setMethod("GET");
        }

        getParams():Object {
            return {};
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "list");
        }
    }
}