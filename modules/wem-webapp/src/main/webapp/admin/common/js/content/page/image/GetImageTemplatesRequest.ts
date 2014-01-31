module api.content.page.image {

    export class GetImageTemplatesRequest extends ImageTemplateResource<api.content.page.image.json.ImageTemplateSummaryListJson> {

        private siteTemplateKey: api.content.site.template.SiteTemplateKey;

        constructor(siteTemplateKey: api.content.site.template.SiteTemplateKey) {
            super();
            super.setMethod("GET");
            this.siteTemplateKey = siteTemplateKey;
        }

        getParams(): Object {
            return {
                key: this.siteTemplateKey.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list");
        }

        sendAndParse(): JQueryPromise<ImageTemplateSummary[]> {

            var deferred = jQuery.Deferred<ImageTemplateSummary[]>();

            this.send().
                done((response: api.rest.JsonResponse<api.content.page.image.json.ImageTemplateSummaryListJson>) => {
                    var array:ImageTemplateSummary[] = [];
                    response.getResult().templates.forEach((templateJson:api.content.page.image.json.ImageTemplateSummaryJson) => {
                        array.push(this.fromJsonToImageTemplateSummary(templateJson));
                    });

                    deferred.resolve(array);
                }).fail((response: api.rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}
