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

        sendAndParse(): JQueryPromise<api_content_site_template.SiteTemplateSummary[]> {

            var deferred = jQuery.Deferred<api_content_site_template.SiteTemplateSummary[]>();

            this.send().
                done((response: api_rest.JsonResponse<api_content_site_template_json.SiteTemplateSummaryListJson>) => {
                deferred.resolve(this.fromJsonArrayToSiteTemplateSummaryArray(response.getResult().siteTemplates));
            }).fail((response: api_rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}