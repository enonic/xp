module api.content.page.part{

    export class GetPartTemplateByKeyRequest extends PartTemplateResourceRequest<api.content.page.part.json.PartTemplateJson> {

        private partTemplateKey:api.content.page.part.PartTemplateKey;

        constructor(partTemplateKey:api.content.page.part.PartTemplateKey) {
            super();
            super.setMethod("GET");
            this.partTemplateKey = partTemplateKey;
        }

        getParams():Object {
            return {
                key: this.partTemplateKey.toString()
            };
        }

        getRequestPath():api.rest.Path {
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
