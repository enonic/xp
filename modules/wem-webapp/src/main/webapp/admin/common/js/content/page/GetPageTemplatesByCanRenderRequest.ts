module api.content.page {

    export class GetPageTemplatesByCanRenderRequest extends PageTemplateResourceRequest<api.content.page.json.PageTemplateSummaryListJson> {

        private siteTemplateKey: api.content.site.template.SiteTemplateKey;

        private contentTypeName: api.schema.content.ContentTypeName;

        constructor(siteTemplateKey:api.content.site.template.SiteTemplateKey, contentName:api.schema.content.ContentTypeName) {
            super();
            this.setMethod("GET");
            this.siteTemplateKey = siteTemplateKey;
            this.contentTypeName = contentName;
        }

        getParams():Object {
            return {
                key: this.siteTemplateKey.toString(),
                contentTypeName: this.contentTypeName.toString()
            }
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "listByCanRender");
        }

        sendAndParse(): JQueryPromise<api.content.page.PageTemplateSummary[]> {

            var deferred = jQuery.Deferred<api.content.page.PageTemplateSummary[]>();

            this.send().
                done((response: api.rest.JsonResponse<api.content.page.json.PageTemplateSummaryListJson>) => {
                         var array:api.content.page.PageTemplateSummary[] = [];
                         response.getResult().templates.forEach((templateJson:api.content.page.json.PageTemplateSummaryJson) => {
                             array.push(this.fromJsonToPageTemplateSummary(templateJson));
                         });

                         deferred.resolve(array);
                     }).fail((response: api.rest.RequestError) => {
                                 deferred.reject(null);
                             });

            return deferred;
        }
    }
}