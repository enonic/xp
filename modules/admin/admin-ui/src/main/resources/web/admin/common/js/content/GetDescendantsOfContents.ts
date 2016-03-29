module api.content {

    export class GetDescendantsOfContents extends ContentResourceRequest<ListContentResult<api.content.json.ContentSummaryJson>, ContentResponse<ContentSummary>> {

        private contentPaths: ContentPath[] = [];

        constructor(contentPath?: ContentPath) {
            super();
            super.setMethod("POST");
            if (contentPath) {
                this.addContentPath(contentPath);
            }
        }

        setContentPaths(contentPaths: ContentPath[]): GetDescendantsOfContents {
            this.contentPaths = contentPaths;
            return this;
        }

        addContentPath(contentPath: ContentPath): GetDescendantsOfContents {
            this.contentPaths.push(contentPath);
            return this;
        }

        getParams(): Object {
            var fn = (contentPath: ContentPath) => {
                return contentPath.toString();
            };
            return {
                contentPaths: this.contentPaths.map(fn)
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "getDescendantsOfContents");
        }

        sendAndParse(): wemQ.Promise<ContentResponse<ContentSummary>> {

            return this.send().then((response: api.rest.JsonResponse<ListContentResult<api.content.json.ContentSummaryJson>>) => {
                return new ContentResponse(
                    ContentSummary.fromJsonArray(response.getResult().contents),
                    new ContentMetadata(response.getResult().metadata["hits"], response.getResult().metadata["totalHits"])
                );
            });
        }
    }
}