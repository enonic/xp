module api.content.page.image {

    export class ImageTemplateSummaryLoader extends TemplateSummaryLoader<ImageTemplateSummary> {
        constructor(siteTemplateKey: api.content.site.template.SiteTemplateKey) {
            super();
            this.setRequest(new GetImageTemplatesRequest(siteTemplateKey));
        }

        doRequest(getTemplatesRequest:api.rest.ResourceRequest<any>):Q.Promise<ImageTemplateSummary[]> {
            var deferred = Q.defer<ImageTemplateSummary[]>();

            (<GetImageTemplatesRequest>getTemplatesRequest).sendAndParse()
                .done((templates:PageTemplateSummary[]) => {


                    deferred.resolve(templates)
                });
            return deferred.promise;
        }
    }

}