module api_content_page {

    export class GetPageTemplatesRequest extends PageTemplateResourceRequest<api_content_page_json.PageTemplateSummaryListJson> {

        private siteTemplateKey: api_content_site_template.SiteTemplateKey;

        constructor(siteTemplateKey: api_content_site_template.SiteTemplateKey) {
            super();
            super.setMethod("GET");
            this.siteTemplateKey = siteTemplateKey;
        }

        getParams(): Object {
            return {
                key: this.siteTemplateKey.toString()
            };
        }

        getRequestPath(): api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "list");
        }

        sendAndParse(): JQueryPromise<api_content_page.PageTemplateSummary[]> {

            var deferred = jQuery.Deferred<api_content_page.PageTemplateSummary[]>();

            this.send().
                done((response: api_rest.JsonResponse<api_content_page_json.PageTemplateSummaryListJson>) => {
                var array:api_content_page.PageTemplateSummary[] = [];
                response.getResult().templates.forEach((templateJson:api_content_page_json.PageTemplateSummaryJson) => {
                    array.push(this.fromJsonToPageTemplateSummary(templateJson));
                });

                deferred.resolve(array);
            }).fail((response: api_rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}
