module api_content_page {

    export class GetPageTemplateByKeyRequest extends PageTemplateResourceRequest<api_content_page_json.PageTemplateJson> {

        private pageTemplateKey:api_content_page.PageTemplateKey;

        constructor(pageTemplateKey:api_content_page.PageTemplateKey) {
            super();
            super.setMethod("GET");
            this.pageTemplateKey = pageTemplateKey;
        }

        getParams():Object {
            return {
                key: this.pageTemplateKey.toString()
            };
        }

        getRequestPath():api_rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): JQueryPromise<api_content_page.PageTemplate> {

            var deferred = jQuery.Deferred<api_content_page.PageTemplate>();

            this.send().done((response: api_rest.JsonResponse<api_content_page_json.PageTemplateJson>) => {
                deferred.resolve(this.fromJsonToPageTemplate(response.getResult()));
            }).fail((response: api_rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}