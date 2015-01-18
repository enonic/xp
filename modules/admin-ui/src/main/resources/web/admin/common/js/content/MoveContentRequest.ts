module api.content {

    export class MoveContentRequest extends ContentResourceRequest<json.ContentJson, Content> {

        private id: ContentId;

        private parentPath: ContentPath;

        constructor(id: ContentId, parentPath: ContentPath) {
            super();
            super.setMethod("POST");
            this.id = id;
            this.parentPath = parentPath;
        }

        getParams(): Object {
            return {
                contentId: this.id.toString(),
                parentContentPath: !!this.parentPath ? this.parentPath.toString() : ""
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "move");
        }

        sendAndParse(): wemQ.Promise<Content> {

            return this.send().then((response: api.rest.JsonResponse<json.ContentJson>) => {
                return this.fromJsonToContent(response.getResult());
            });
        }
    }
}