module api.content.resource {

    import HasUnpublishedChildrenListJson = api.content.json.HasUnpublishedChildrenListJson;
    import HasUnpublishedChildrenResult = api.content.resource.result.HasUnpublishedChildrenResult;

    export class HasUnpublishedChildrenRequest
            extends ContentResourceRequest<HasUnpublishedChildrenListJson, HasUnpublishedChildrenResult> {

        private ids: ContentId[] = [];

        constructor(ids:ContentId[]) {
            super();
            super.setMethod('POST');
            this.ids = ids;
        }

        getParams(): Object {
            return {
                contentIds: this.ids.map(id => id.toString())
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'hasUnpublishedChildren');
        }

        sendAndParse(): wemQ.Promise<HasUnpublishedChildrenResult> {

            return this.send().then((response: api.rest.JsonResponse<HasUnpublishedChildrenListJson>) => {
                return HasUnpublishedChildrenResult.fromJson(response.getResult());
            });
        }
    }
}
