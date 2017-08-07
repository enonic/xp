module api.schema.content {

    export class GetAllContentTypesRequest
        extends ContentTypeResourceRequest<ContentTypeSummaryListJson, ContentTypeSummary[]> {

        private inlineMixinsToFormItems: boolean = true;
        private contextDependent: boolean = false;
        private contentId: api.content.ContentId;

        constructor() {
            super();
            super.setMethod('GET');
        }

        setContextDependent(value: boolean): void {
            this.contextDependent = value;
        }

        setContentContext(value: ContentId): void {
            this.contentId = value;
        }

        getParams(): Object {
            return {
                inlineMixinsToFormItems: this.inlineMixinsToFormItems,
                context: this.contextDependent,
                contentId: this.contentId && this.contentId.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'all');
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
