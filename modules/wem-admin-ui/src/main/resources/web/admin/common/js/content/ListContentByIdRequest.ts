module api.content {

    export class ListContentByIdRequest extends ContentResourceRequest<ListContentResult<api.content.json.ContentSummaryJson>, ContentSummary[]> {

        private parentId:string;

        private expand:api.rest.Expand = api.rest.Expand.SUMMARY;

        private from: number;

        private size: number;

        constructor(parentId:string) {
            super();
            super.setMethod("GET");
            this.parentId = parentId;
        }

        setExpand(value:api.rest.Expand) {
            this.expand = value;
        }

        setFrom(value: number) {
            this.from = value;
        }

        setSize(value: number) {
            this.size = value;
        }

        getParams():Object {
            return {
                parentId: this.parentId,
                expand: this.expand,
                from: this.from,
                size: this.size
            };
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list");
        }

        sendAndParse(): wemQ.Promise<ContentSummary[]> {

            return this.send().then((response:api.rest.JsonResponse<ListContentResult<api.content.json.ContentSummaryJson>>) => {
                return api.content.ContentSummary.fromJsonArray( response.getResult().contents );
            });
        }
    }
}