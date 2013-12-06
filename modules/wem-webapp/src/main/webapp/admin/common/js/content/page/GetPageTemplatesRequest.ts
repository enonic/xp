module api_content_page {

    export class GetPageTemplatesRequest extends PageTemplateResourceRequest<api_content_page_json.PageTemplateListJson> {

        private siteTemplateKey: api_content_site_template.SiteTemplateKey;

        constructor(siteTemplateKey: api_content_site_template.SiteTemplateKey) {
            super();
            super.setMethod("GET");

            this.siteTemplateKey = siteTemplateKey;
        }

        getParams(): Object {
            return {
                siteTemplateKey: this.siteTemplateKey.toString()
            };
        }

        getRequestPath(): api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "list");
        }

        sendAndParse(): JQueryPromise<api_content_page.PageTemplate[]> {

            var deferred = jQuery.Deferred<api_content_page.PageTemplate>();

            this.send().
                done((response: api_rest.JsonResponse<api_content_page_json.PageTemplateListJson>) => {
                var array:api_content_page.PageTemplate[] = [];
                response.getResult().templates.forEach((templateJson:api_content_page_json.PageTemplateJson) => {
                    array.push(this.fromJsonToPageTemplate(templateJson));
                });

                deferred.resolve(array);
            }).fail((response: api_rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}
