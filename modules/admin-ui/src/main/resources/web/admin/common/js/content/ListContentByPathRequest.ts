module api.content {

    export class ListContentByPathRequest<T> extends ContentResourceRequest<ListContentResult<api.content.json.ContentSummaryJson>, ContentResponse<ContentSummary>> {

        private parentPath:ContentPath;

        private expand:api.rest.Expand = api.rest.Expand.SUMMARY;

        private from: number;

        private size: number;

        constructor(parentPath: ContentPath) {
            super();
            super.setMethod("GET");
            this.parentPath = parentPath;
        }

        setExpand(value:api.rest.Expand): ListContentByPathRequest<T> {
            this.expand = value;
            return this;
        }

        setFrom(value: number): ListContentByPathRequest<T> {
            this.from = value;
            return this;
        }

        setSize(value: number): ListContentByPathRequest<T> {
            this.size = value;
            return this;
        }

        getParams(): Object {
            return {
                parentPath: this.parentPath.toString(),
                expand: this.expand,
                from: this.from,
                size: this.size
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list", "bypath");
        }

        sendAndParse(): wemQ.Promise<ContentResponse<ContentSummary>> {

            return this.send().then((response: api.rest.JsonResponse<ListContentResult<api.content.json.ContentSummaryJson>>) => {
                return new ContentResponse(
                    ContentSummary.fromJsonArray(response.getResult().contents, this.propertyIdProvider),
                    new ContentMetadata(response.getResult().metadata["hits"], response.getResult().metadata["totalHits"])
                );
            });
        }

    }
}