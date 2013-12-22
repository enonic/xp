module api_content_site_template {

    export class GetSiteTemplateRequest extends SiteTemplateResourceRequest<api_content_site_template_json.SiteTemplateJson> {

        private key: SiteTemplateKey;

        constructor(key: SiteTemplateKey) {
            super();
            super.setMethod("GET");
            this.key = key;
        }

        getParams(): Object {
            return {
                siteTemplateKey: this.key.toString()
            };
        }

        getRequestPath(): api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath());
        }

        sendAndParse(): JQueryPromise<api_content_site_template.SiteTemplate> {

            var deferred = jQuery.Deferred<api_content_site_template.SiteTemplate>();

            this.send().
                done((response: api_rest.JsonResponse<api_content_site_template_json.SiteTemplateJson>) => {
                    deferred.resolve(this.fromJsonToSiteTemplate(response.getResult()));
                }).fail((response: api_rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}