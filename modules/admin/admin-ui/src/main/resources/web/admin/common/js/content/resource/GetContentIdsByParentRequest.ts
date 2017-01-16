module api.content.resource {

    import ListContentResult = api.content.resource.result.ListContentResult;
    import ContentResponse = api.content.resource.result.ContentResponse;
    import ChildOrder = api.content.order.ChildOrder;

    export class GetContentIdsByParentRequest extends ContentResourceRequest<any, any> {

        private parentId: ContentId;

        private order: api.content.order.ChildOrder;

        constructor() {
            super();
            super.setMethod('GET');
        }

        setOrder(value: api.content.order.ChildOrder): GetContentIdsByParentRequest {
            this.order = value;
            return this;
        }

        setParentId(value: ContentId): GetContentIdsByParentRequest {
            this.parentId = value;
            return this;
        }

        getParams(): Object {
            return {
                parentId: this.parentId ? this.parentId.toString() : null,
                childOrder: !!this.order ? this.order.toString() : ''
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'listIds');
        }

        sendAndParse(): wemQ.Promise<ContentId[]> {

            return this.send().then((response: api.rest.JsonResponse<api.content.json.ContentIdBaseItemJson[]>) => {
                return response.getResult().map((item => new ContentId(item.id)));
            });
        }
    }
}
