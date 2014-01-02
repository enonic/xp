module api.content.page.layout{

    export class GetLayoutTemplateByKeyRequest extends LayoutTemplateResourceRequest<api.content.page.layout.json.LayoutTemplateJson> {

        private layoutTemplateKey:api.content.page.layout.LayoutTemplateKey;

        constructor(layoutTemplateKey:api.content.page.layout.LayoutTemplateKey) {
            super();
            super.setMethod("GET");
            this.layoutTemplateKey = layoutTemplateKey;
        }

        getParams():Object {
            return {
                key: this.layoutTemplateKey.toString()
            };
        }

        getRequestPath():api.rest.Path {
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
