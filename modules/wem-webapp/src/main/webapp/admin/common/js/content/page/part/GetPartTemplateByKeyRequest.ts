module api.content.page.part {

    export class GetPartTemplateByKeyRequest extends PartTemplateResourceRequest<api.content.page.part.json.PartTemplateJson> {

        private siteTemplateKey: api.content.site.template.SiteTemplateKey;

        private partTemplateKey: api.content.page.TemplateKey;

        constructor(partTemplateKey: api.content.page.TemplateKey) {
            super();
            super.setMethod("GET");
            this.partTemplateKey = partTemplateKey;
        }

        setSiteTemplateKey(value: api.content.site.template.SiteTemplateKey): GetPartTemplateByKeyRequest {
            this.siteTemplateKey = value;
            return this;
        }

        getParams(): Object {
            return {
                siteTemplateKey: this.siteTemplateKey.toString(),
                key: this.partTemplateKey.toString()
            };
        }

        validate() {
            api.util.assertNotNull(this.siteTemplateKey, "siteTemplateKey cannot be null");
            api.util.assertNotNull(this.partTemplateKey, "partTemplateKey cannot be null");
        }

        getRequestPath(): api.rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): JQueryPromise<api.content.page.part.PartTemplate> {

            var deferred = jQuery.Deferred<api.content.page.part.PartTemplate>();

            this.send().
                done((response: api.rest.JsonResponse<api.content.page.part.json.PartTemplateJson>) => {
                    deferred.resolve(this.fromJsonToPartTemplate(response.getResult()));
                }).fail((response: api.rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}
