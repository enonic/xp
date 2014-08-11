module api.content.site.template {

    import TemplateSummary = api.content.TemplateSummary;

    export class TemplateTreeRequest extends SiteTemplateResourceRequest<TemplateSummaryListJson, TemplateSummary[]> {

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

            return this.send().then((response: api.rest.JsonResponse<TemplateSummaryListJson>) => {
                return this.fromJsonArrayToTemplateSummaryArray(response.getResult().templates);
            });
        }

        private fromJsonArrayToTemplateSummaryArray(jsonArray: TemplateSummaryJson[]): TemplateSummary[] {

            var summaryArray: TemplateSummary[] = [];
            jsonArray.forEach((summaryJson: TemplateSummaryJson) => {
                summaryArray.push(TemplateSummary.fromJson(summaryJson));
            });
            return summaryArray;
        }
    }
}