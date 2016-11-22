module api.content.resource {

    import ListContentResult = api.content.resource.result.ListContentResult;
    import ContentResponse = api.content.resource.result.ContentResponse;

    export class ListContentByIdRequest<CONTENT_JSON extends api.content.json.ContentSummaryJson,CONTENT extends api.content.ContentSummary> extends ContentResourceRequest<ListContentResult<CONTENT_JSON>, ContentResponse<CONTENT>> {

        private parentId: ContentId;

        private expand: api.rest.Expand = api.rest.Expand.SUMMARY;

        private from: number;

        private size: number;

        private order: api.content.order.ChildOrder;

        constructor(parentId: ContentId) {
            super();
            super.setMethod("GET");
            this.parentId = parentId;
        }

        setExpand(value: api.rest.Expand): ListContentByIdRequest<CONTENT_JSON,CONTENT> {
            this.expand = value;
            return this;
        }

        setFrom(value: number): ListContentByIdRequest<CONTENT_JSON,CONTENT> {
            this.from = value;
            return this;
        }

        setSize(value: number): ListContentByIdRequest<CONTENT_JSON,CONTENT> {
            this.size = value;
            return this;
        }

        setOrder(value: api.content.order.ChildOrder): ListContentByIdRequest<CONTENT_JSON,CONTENT> {
            this.order = value;
            return this;
        }

        getParams(): Object {
            return {
                parentId: this.parentId ? this.parentId.toString() : null,
                expand: api.rest.Expand[this.expand],
                from: this.from,
                size: this.size,
                childOrder: !!this.order ? this.order.toString() : ""
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list");
        }

        sendAndParse(): wemQ.Promise<ContentResponse<CONTENT>> {

            return this.send().then((response: api.rest.JsonResponse<ListContentResult<CONTENT_JSON>>) => {
                var contents: CONTENT[];
                if (this.expand === api.rest.Expand.FULL) {
                    contents = <any[]>Content.fromJsonArray(<any[]>response.getResult().contents);
                }
                else {
                    contents = <any[]>ContentSummary.fromJsonArray(response.getResult().contents);
                }
                return new ContentResponse(
                    contents,
                    new ContentMetadata(response.getResult().metadata["hits"], response.getResult().metadata["totalHits"])
                );
            });
        }
    }
}