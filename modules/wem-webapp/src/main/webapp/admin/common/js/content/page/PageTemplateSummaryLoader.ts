module api.content.page {

    export class PageTemplateSummaryLoader extends TemplateSummaryLoader<PageTemplateSummary> {
        constructor(siteTemplateKey:api.content.site.template.SiteTemplateKey) {
            super();
            this.setRequest(new GetPageTemplatesRequest(siteTemplateKey));
        }

        doRequest(getTemplatesRequest:api.rest.ResourceRequest<any>):Q.Promise<PageTemplateSummary[]> {
            var deferred = Q.defer<PageTemplateSummary[]>();

            (<GetPageTemplatesRequest>getTemplatesRequest).sendAndParse()
                .done((templates:PageTemplateSummary[]) => {


                    deferred.resolve(templates)
                });
            return deferred.promise;
        }
    }

}