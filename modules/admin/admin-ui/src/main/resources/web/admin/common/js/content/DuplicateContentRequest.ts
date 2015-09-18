module api.content {

    export class DuplicateContentRequest extends ContentResourceRequest<json.ContentJson, Content> {

        private id: ContentId;

        constructor(id: ContentId) {
            super();
            super.setMethod("POST");
            this.id = id;
        }

        getParams(): Object {
            return {
                contentId: this.id.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "duplicate");
        }

        sendAndParse(): wemQ.Promise<Content> {

            return this.send().then((response: api.rest.JsonResponse<json.ContentJson>) => {
                return this.fromJsonToContent(response.getResult());
            });
        }
    }
}