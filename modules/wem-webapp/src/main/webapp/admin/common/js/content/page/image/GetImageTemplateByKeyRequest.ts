module api.content.page.image{

    export class GetImageTemplateByKeyRequest extends ImageTemplateResource<api.content.page.image.json.ImageTemplateJson> {

        private imageTemplateKey:api.content.page.image.ImageTemplateKey;

        constructor(imageTemplateKey:api.content.page.image.ImageTemplateKey) {
            super();
            super.setMethod("GET");
            this.imageTemplateKey = imageTemplateKey;
        }

        getParams():Object {
            return {
                key: this.imageTemplateKey.toString()
            };
        }

        getRequestPath():api.rest.Path {
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
