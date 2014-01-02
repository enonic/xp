module api.content.page {

    export class GetPageTemplatesRequest extends PageTemplateResourceRequest<api.content.page.json.PageTemplateSummaryListJson> {

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

        sendAndParse(): JQueryPromise<api.content.page.PageTemplateSummary[]> {

            var deferred = jQuery.Deferred<api.content.page.PageTemplateSummary[]>();

            this.send().
                done((response: api.rest.JsonResponse<api.content.page.json.PageTemplateSummaryListJson>) => {
                var array:api.content.page.PageTemplateSummary[] = [];
                response.getResult().templates.forEach((templateJson:api.content.page.json.PageTemplateSummaryJson) => {
                    array.push(this.fromJsonToPageTemplateSummary(templateJson));
                });

                deferred.resolve(array);
            }).fail((response: api.rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}
