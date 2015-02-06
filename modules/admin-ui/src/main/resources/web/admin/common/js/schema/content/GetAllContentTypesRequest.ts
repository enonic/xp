module api.schema.content {

    export class GetAllContentTypesRequest extends ContentTypeResourceRequest<ContentTypeSummaryListJson, ContentTypeSummary[]> {

        private inlinesToFormItems:boolean = true;

        constructor() {
            super();
            super.setMethod("GET");
        }

        getParams():Object {
            return {
                inlinesToFormItems: this.inlinesToFormItems
            };
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "all");
        }

        sendAndParse(): wemQ.Promise<ContentTypeSummary[]> {

            return this.send().then((response: api.rest.JsonResponse<ContentTypeSummaryListJson>) => {
                return response.getResult().contentTypes.map((contentTypeJson: ContentTypeSummaryJson) => {
                    return this.fromJsonToContentTypeSummary(contentTypeJson);
                });
            });
        }
    }
}