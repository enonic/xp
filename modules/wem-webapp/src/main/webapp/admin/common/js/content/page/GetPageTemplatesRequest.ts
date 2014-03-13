module api.content.page {

    export class GetPageTemplatesRequest extends PageTemplateResourceRequest<api.content.page.PageTemplateSummaryListJson> {

        private siteTemplateKey: api.content.site.template.SiteTemplateKey;

        constructor(siteTemplateKey: api.content.site.template.SiteTemplateKey) {
            super();
            super.setMethod("GET");
            this.siteTemplateKey = siteTemplateKey;
        }

        getParams(): Object {
            return {
                key: this.siteTemplateKey.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list");
        }

        sendAndParse(): Q.Promise<api.content.page.PageTemplateSummary[]> {

            var deferred = Q.defer<api.content.page.PageTemplateSummary[]>();

            this.send().
                then((response: api.rest.JsonResponse<api.content.page.PageTemplateSummaryListJson>) => {
                var array:api.content.page.PageTemplateSummary[] = [];
                response.getResult().templates.forEach((templateJson:api.content.page.PageTemplateSummaryJson) => {
                    array.push(this.fromJsonToPageTemplateSummary(templateJson));
                });

                deferred.resolve(array);
            }).catch((response: api.rest.RequestError) => {
                    deferred.reject(null);
                }).done();

            return deferred.promise;
        }
    }
}
