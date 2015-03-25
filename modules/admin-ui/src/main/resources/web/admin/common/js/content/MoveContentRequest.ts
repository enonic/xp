module api.content {

    export class MoveContentRequest extends ContentResourceRequest<MoveContentResult<api.content.json.ContentSummaryJson>, ContentResponse<ContentSummary>> {

        private ids: ContentIds;

        private parentPath: ContentPath;

        constructor(id: ContentIds, parentPath: ContentPath) {
            super();
            super.setMethod("POST");
            this.ids = id;
            this.parentPath = parentPath;
        }

        getParams(): Object {
            var fn = (contentId:ContentId) => {
                return contentId.toString();
            };
            return {
                contentIds: this.ids.map(fn),
                parentContentPath: !!this.parentPath ? this.parentPath.toString() : ""
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "move");
        }

        sendAndParse(): wemQ.Promise<ContentResponse<ContentSummary>> {

            return this.send().then((response: api.rest.JsonResponse<MoveContentResult<api.content.json.ContentSummaryJson>>) => {
                return new ContentResponse(
                    ContentSummary.fromJsonArray(response.getResult().contents, this.propertyIdProvider),
                    new ContentMetadata(response.getResult().metadata["hits"], response.getResult().metadata["totalHits"])
                );
            });
        }
    }
}