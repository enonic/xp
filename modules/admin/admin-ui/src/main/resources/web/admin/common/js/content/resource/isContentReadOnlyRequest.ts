module api.content.resource {

    export class IsContentReadOnlyRequest extends ContentResourceRequest<string[], string[]> {

        private ids: ContentId[];

        constructor(ids: ContentId[]) {
            super();
            super.setMethod("POST");
            this.ids = ids;
        }

        getParams(): Object {
            return {
                contentIds: this.ids.map(id => id.toString())
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'isReadOnlyContent');
        }

        sendAndParse(): wemQ.Promise<string[]> {
            return this.send().then((response: api.rest.JsonResponse<string[]>) => {
                return response.getResult();
            });
        }
    }
}
