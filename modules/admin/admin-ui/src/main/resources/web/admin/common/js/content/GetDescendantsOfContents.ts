module api.content {

    export class GetDescendantsOfContents extends ContentResourceRequest<ListContentResult<api.content.json.ContentSummaryJson>, ContentResponse<ContentSummary>> {

        private contentPaths: ContentPath[] = [];

        private filterStatuses: CompareStatus[] = [];

        private from: number;

        private size: number;

        public static LOAD_SIZE: number = 20;

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

        setFilterStatuses(filterStatuses: CompareStatus[]): GetDescendantsOfContents {
            this.filterStatuses = filterStatuses;
            return this;
        }

        addContentPath(contentPath: ContentPath): GetDescendantsOfContents {
            this.contentPaths.push(contentPath);
            return this;
        }

        setFrom(value: number): GetDescendantsOfContents {
            this.from = value;
            return this;
        }

        setSize(value: number): GetDescendantsOfContents {
            this.size = value;
            return this;
        }

        getParams(): Object {
            var fn = (contentPath: ContentPath) => {
                return contentPath.toString();
            };
            return {
                contentPaths: this.contentPaths.map(fn),
                filterStatuses: this.filterStatuses,
                from: this.from || 0,
                size: this.size || GetDescendantsOfContents.LOAD_SIZE
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