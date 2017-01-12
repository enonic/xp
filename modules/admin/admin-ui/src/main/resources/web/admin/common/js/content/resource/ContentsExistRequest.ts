module api.content.resource {

    import ContentsExistJson = api.content.json.ContentsExistJson;
    import ContentsExistResult = api.content.resource.result.ContentsExistResult;

    export class ContentsExistRequest extends ContentResourceRequest<ContentsExistJson, ContentsExistResult> {

        private contentIds: string[] = [];

        constructor(contentIds: string[]) {
            super();
            super.setMethod("POST");
            this.contentIds = contentIds;
        }

        getParams(): Object {
            return {
                contentIds: this.contentIds
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "contentsExist");
        }

        sendAndParse(): wemQ.Promise<ContentsExistResult> {

            return this.send().then((response: api.rest.JsonResponse<ContentsExistJson>) => {
                return new ContentsExistResult(response.getResult());
            });
        }
    }
}
