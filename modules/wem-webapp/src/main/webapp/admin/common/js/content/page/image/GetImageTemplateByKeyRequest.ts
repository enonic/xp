module api.content.page.image {

    export class GetImageTemplateByKeyRequest extends ImageTemplateResource<api.content.page.image.json.ImageTemplateJson> {

        private siteTemplateKey: api.content.site.template.SiteTemplateKey;

        private imageTemplateKey: api.content.page.image.ImageTemplateKey;

        constructor(imageTemplateKey: api.content.page.image.ImageTemplateKey) {
            super();
            super.setMethod("GET");
            this.imageTemplateKey = imageTemplateKey;
        }

        setSiteTemplateKey(value: api.content.site.template.SiteTemplateKey): GetImageTemplateByKeyRequest {
            this.siteTemplateKey = value;
            return this;
        }

        getParams(): Object {
            return {
                siteTemplateKey: this.siteTemplateKey.toString(),
                key: this.imageTemplateKey.toString()
            };
        }

        validate() {
            api.util.assertNotNull(this.siteTemplateKey, "siteTemplateKey cannot be null");
            api.util.assertNotNull(this.imageTemplateKey, "imageTemplateKey cannot be null");
        }

        getRequestPath(): api.rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): JQueryPromise<api.content.page.image.ImageTemplate> {

            var deferred = jQuery.Deferred<api.content.page.image.ImageTemplate>();

            this.send().
                done((response: api.rest.JsonResponse<api.content.page.image.json.ImageTemplateJson>) => {
                    deferred.resolve(this.fromJsonToImageTemplate(response.getResult()));
                }).fail((response: api.rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}
