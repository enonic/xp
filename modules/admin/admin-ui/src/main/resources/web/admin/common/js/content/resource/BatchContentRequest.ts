module api.content.resource {

    import BatchContentResult = api.content.resource.result.BatchContentResult;
    import ContentResponse = api.content.resource.result.ContentResponse;
    import ContentSummaryJson = api.content.json.ContentSummaryJson;

    export class BatchContentRequest
    extends ContentResourceRequest<BatchContentResult<ContentSummaryJson>, ContentResponse<ContentSummary>> {

        private contentPaths: ContentPath[] = [];

        constructor(contentPath?: ContentPath) {
            super();
            super.setMethod("POST");
            if (contentPath) {
                this.addContentPath(contentPath);
            }
        }

        setContentPaths(contentPaths: ContentPath[]): BatchContentRequest {
            this.contentPaths = contentPaths;
            return this;
        }

        addContentPath(contentPath: ContentPath): BatchContentRequest {
            this.contentPaths.push(contentPath);
            return this;
        }

        getParams(): Object {
            let fn = (contentPath: ContentPath) => {
                return contentPath.toString();
            };
            return {
                contentPaths: this.contentPaths.map(fn)
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "batch");
        }

        sendAndParse(): wemQ.Promise<ContentResponse<ContentSummary>> {

            return this.send().then((response: api.rest.JsonResponse<BatchContentResult<api.content.json.ContentSummaryJson>>) => {
                return new ContentResponse(
                    ContentSummary.fromJsonArray(response.getResult().contents),
                    new ContentMetadata(response.getResult().metadata["hits"], response.getResult().metadata["totalHits"])
                );
            });
        }
    }
}
