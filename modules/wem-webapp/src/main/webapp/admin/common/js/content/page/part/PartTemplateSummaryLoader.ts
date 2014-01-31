module api.content.page.part {

    export class PartTemplateSummaryLoader extends TemplateSummaryLoader<PartTemplateSummary> {
        constructor(siteTemplateKey: api.content.site.template.SiteTemplateKey) {
            super();
            this.setRequest(new GetPartTemplatesRequest(siteTemplateKey));
        }

        doRequest(getTemplatesRequest:api.rest.ResourceRequest<any>):Q.Promise<PartTemplateSummary[]> {
            var deferred = Q.defer<PartTemplateSummary[]>();

            (<GetPartTemplatesRequest>getTemplatesRequest).sendAndParse()
                .done((templates:PartTemplateSummary[]) => {


                    deferred.resolve(templates)
                });
            return deferred.promise;
        }
    }

}