module api.content {

    import AccessControlList = api.security.acl.AccessControlList;

    export class GetContentPermissionsByIdRequest extends ContentResourceRequest<json.ContentPermissionsJson, AccessControlList> {

        private contentId: ContentId;

        constructor(contentId: ContentId) {
            super();
            super.setMethod("GET");
            this.contentId = contentId;
        }

        getParams(): Object {
            return {
                id: this.contentId.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "contentPermissions");
        }

        sendAndParse(): wemQ.Promise<AccessControlList> {

            return this.send().then((response: api.rest.JsonResponse<json.ContentPermissionsJson>) => {
                return AccessControlList.fromJson(response.getResult().permissions);
            });
        }
    }
}