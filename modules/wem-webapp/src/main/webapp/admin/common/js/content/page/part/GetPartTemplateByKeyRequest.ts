module api_content_page_part{

    export class GetPartTemplateByKeyRequest extends PartTemplateResourceRequest<api_content_page_part_json.PartTemplateJson> {

        private partTemplateKey:api_content_page_part.PartTemplateKey;

        constructor(partTemplateKey:api_content_page_part.PartTemplateKey) {
            super();
            super.setMethod("GET");
            this.partTemplateKey = partTemplateKey;
        }

        getParams():Object {
            return {
                key: this.partTemplateKey.toString()
            };
        }

        getRequestPath():api_rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): JQueryPromise<api_content_page_part.PartTemplate> {

            var deferred = jQuery.Deferred<api_content_page_part.PartTemplate>();

            this.send().
                done((response: api_rest.JsonResponse<api_content_page_part_json.PartTemplateJson>) => {
                    deferred.resolve(this.fromJsonToPartTemplate(response.getResult()));
                }).fail((response: api_rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}
