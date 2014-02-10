module api.content.site.template {

    export class GetSiteTemplateRequest extends SiteTemplateResourceRequest<api.content.site.template.json.SiteTemplateJson> {

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

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath());
        }

        sendAndParse(): Q.Promise<api.content.site.template.SiteTemplate> {

            var deferred = Q.defer<api.content.site.template.SiteTemplate>();

            this.send().
                done((response: api.rest.JsonResponse<api.content.site.template.json.SiteTemplateJson>) => {
                    deferred.resolve(this.fromJsonToSiteTemplate(response.getResult()));
                }).fail((response: api.rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred.promise;
        }
    }
}