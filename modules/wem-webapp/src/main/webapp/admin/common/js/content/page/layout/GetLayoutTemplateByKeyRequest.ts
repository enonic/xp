module api_content_page_layout{

    export class GetLayoutTemplateByKeyRequest extends LayoutTemplateResourceRequest<api_content_page_layout_json.LayoutTemplateJson> {

        private layoutTemplateKey:api_content_page_layout.LayoutTemplateKey;

        constructor(layoutTemplateKey:api_content_page_layout.LayoutTemplateKey) {
            super();
            super.setMethod("GET");
            this.layoutTemplateKey = layoutTemplateKey;
        }

        getParams():Object {
            return {
                key: this.layoutTemplateKey.toString()
            };
        }

        getRequestPath():api_rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): JQueryPromise<api_content_page_layout.LayoutTemplate> {

            var deferred = jQuery.Deferred<api_content_page_layout.LayoutTemplate>();

            this.send().
                done((response: api_rest.JsonResponse<api_content_page_layout_json.LayoutTemplateJson>) => {
                    deferred.resolve(this.fromJsonToLayoutTemplate(response.getResult()));
                }).fail((response: api_rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}
