module api.content {

    export class MoveContentRequest extends ContentResourceRequest<MoveContentResultJson, MoveContentResult> {

        private ids: ContentIds;

        private parentPath: ContentPath;

        constructor(id: ContentIds, parentPath: ContentPath) {
            super();
            super.setMethod("POST");
            this.ids = id;
            this.parentPath = parentPath;
        }

        getParams(): Object {
            var fn = (contentId:ContentId) => {
                return contentId.toString();
            };
            return {
                contentIds: this.ids.map(fn),
                parentContentPath: !!this.parentPath ? this.parentPath.toString() : ""
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "move");
        }

        sendAndParse(): wemQ.Promise<MoveContentResult> {

            return this.send().then((response: api.rest.JsonResponse<MoveContentResultJson>) => {
                return MoveContentResult.fromJson(response.getResult());
            });
        }
    }
}