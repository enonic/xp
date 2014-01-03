module api.content.site.template {

    export class GetAllSiteTemplatesRequest extends SiteTemplateResourceRequest<api.content.site.template.json.SiteTemplateSummaryListJson> {

        constructor() {
            super();
            super.setMethod("GET");
        }

        getParams():Object {
            return {};
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list");
        }

        sendAndParse(): JQueryPromise<api.content.site.template.SiteTemplateSummary[]> {

            var deferred = jQuery.Deferred<api.content.site.template.SiteTemplateSummary[]>();

            this.send().
                done((response: api.rest.JsonResponse<api.content.site.template.json.SiteTemplateSummaryListJson>) => {
                deferred.resolve(this.fromJsonArrayToSiteTemplateSummaryArray(response.getResult().siteTemplates));
            }).fail((response: api.rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}