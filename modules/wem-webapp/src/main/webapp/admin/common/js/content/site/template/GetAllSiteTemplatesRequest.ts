module api.content.site.template {

    export class GetAllSiteTemplatesRequest extends SiteTemplateResourceRequest<api.content.site.template.SiteTemplateSummaryListJson> {

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

        sendAndParse(): Q.Promise<api.content.site.template.SiteTemplateSummary[]> {

            var deferred = Q.defer<api.content.site.template.SiteTemplateSummary[]>();

            this.send().
                then((response: api.rest.JsonResponse<api.content.site.template.SiteTemplateSummaryListJson>) => {
                deferred.resolve(this.fromJsonArrayToSiteTemplateSummaryArray(response.getResult().siteTemplates));
            }).catch((response: api.rest.RequestError) => {
                    deferred.reject(null);
                }).done();

            return deferred.promise;
        }
    }
}