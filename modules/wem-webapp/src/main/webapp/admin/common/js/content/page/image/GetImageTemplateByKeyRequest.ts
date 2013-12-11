module api_content_page_image{

    export class GetImageTemplateByKeyRequest extends ImageTemplateResource<api_content_page_image_json.ImageTemplateJson> {

        private imageTemplateKey:api_content_page_image.ImageTemplateKey;

        constructor(imageTemplateKey:api_content_page_image.ImageTemplateKey) {
            super();
            super.setMethod("GET");
            this.imageTemplateKey = imageTemplateKey;
        }

        getParams():Object {
            return {
                key: this.imageTemplateKey.toString()
            };
        }

        getRequestPath():api_rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): JQueryPromise<api_content_page_image.ImageTemplate> {

            var deferred = jQuery.Deferred<api_content_page_image.ImageTemplate>();

            this.send().
                done((response: api_rest.JsonResponse<api_content_page_image_json.ImageTemplateJson>) => {
                    deferred.resolve(this.fromJsonToImageTemplate(response.getResult()));
                }).fail((response: api_rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}
