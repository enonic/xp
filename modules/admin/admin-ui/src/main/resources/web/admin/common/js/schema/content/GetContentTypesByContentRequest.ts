module api.schema.content {

    export class GetContentTypesByContentRequest
        extends ContentTypeResourceRequest<ContentTypeSummaryListJson, ContentTypeSummary[]> {

        private contentId: api.content.ContentId;

        constructor(content: ContentId) {
            super();
            super.setMethod('GET');
            this.contentId = content;
        }

        getParams(): Object {
            return {
                contentId: this.contentId && this.contentId.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'byContent');
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
