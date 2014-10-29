module api.content {

    export class GetContentPermissionsRequest extends ContentResourceRequest<json.ContentPermissionsJson, ContentPermissions> {

        private contentId: ContentId;

        constructor(contentId: ContentId) {
            super();
            super.setMethod("POST");
            this.contentId = contentId;
        }

        getParams(): Object {
            return {
                id: this.contentId.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'getPermissions');
        }

        sendAndParse(): wemQ.Promise<ContentPermissions> {

            return this.send().then((response: api.rest.JsonResponse<json.ContentPermissionsJson>) => {
                return ContentPermissions.fromJson(response.getResult());
            });
        }

    }
}