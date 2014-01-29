module api.content.page.layout {

    export class GetLayoutTemplateByKeyRequest extends LayoutTemplateResourceRequest<api.content.page.layout.json.LayoutTemplateJson> {

        private siteTemplateKey: api.content.site.template.SiteTemplateKey;

        private layoutTemplateKey: api.content.page.layout.LayoutTemplateKey;

        constructor(layoutTemplateKey: api.content.page.layout.LayoutTemplateKey) {
            super();
            super.setMethod("GET");
            this.layoutTemplateKey = layoutTemplateKey;
        }

        setSiteTemplateKey(value: api.content.site.template.SiteTemplateKey): GetLayoutTemplateByKeyRequest {
            this.siteTemplateKey = value;
            return this;
        }

        getParams(): Object {
            return {
                siteTemplateKey: this.siteTemplateKey.toString(),
                key: this.layoutTemplateKey.toString()
            };
        }

        validate() {
            api.util.assertNotNull(this.siteTemplateKey, "siteTemplateKey cannot be null");
            api.util.assertNotNull(this.layoutTemplateKey, "layoutTemplateKey cannot be null");
        }

        getRequestPath(): api.rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): JQueryPromise<api.content.page.layout.LayoutTemplate> {

            var deferred = jQuery.Deferred<api.content.page.layout.LayoutTemplate>();

            this.send().
                done((response: api.rest.JsonResponse<api.content.page.layout.json.LayoutTemplateJson>) => {
                    deferred.resolve(this.fromJsonToLayoutTemplate(response.getResult()));
                }).fail((response: api.rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}
