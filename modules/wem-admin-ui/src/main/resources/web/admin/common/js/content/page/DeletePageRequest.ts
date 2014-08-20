module api.content.page {

    export class DeletePageRequest extends PageResourceRequest<api.content.json.ContentJson, api.content.Content> implements PageCUDRequest {

        private contentId: api.content.ContentId;

        constructor(contentId: api.content.ContentId) {
            super();
            super.setMethod("GET");
            this.contentId = contentId;
        }

        getParams(): Object {
            return {
                contentId: this.contentId.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "delete");
        }

        sendAndParse(): wemQ.Promise<api.content.Content> {

            return this.send().then((response: api.rest.JsonResponse<api.content.json.ContentJson>) => {
                return response.isBlank() ? null : this.fromJsonToContent(response.getResult());
            });
        }
    }
}