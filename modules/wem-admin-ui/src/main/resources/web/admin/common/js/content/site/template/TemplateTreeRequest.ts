module api.content.site.template {

    import TemplateSummary = api.content.TemplateSummary;

    export class TemplateTreeRequest extends SiteTemplateResourceRequest<api.content.site.template.SiteTemplateSummaryListJson> {

        private parentId: string;

        constructor(parentId: string) {
            super();
            super.setMethod("GET");
            this.parentId = parentId;
        }

        getParams(): Object {
            return {
                parentId: this.parentId
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "tree");
        }

        sendAndParse(): Q.Promise<TemplateSummary[]> {

            return this.send().then((response: api.rest.JsonResponse<api.content.site.template.TemplateSummaryListJson>) => {
                return this.fromJsonArrayToTemplateSummaryArray(response.getResult().templates);
            });
        }

        private fromJsonArrayToTemplateSummaryArray(jsonArray: api.content.site.template.TemplateSummaryJson[]): TemplateSummary[] {

            var summaryArray: TemplateSummary[] = [];
            jsonArray.forEach((summaryJson: api.content.site.template.TemplateSummaryJson) => {
                summaryArray.push(TemplateSummary.fromJson(summaryJson));
            });
            return summaryArray;
        }
    }
}