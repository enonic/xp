module api.content.resource {

    import AccessControlList = api.security.acl.AccessControlList;
    import PermissionsJson = api.content.json.PermissionsJson;

    export class GetContentPermissionsByIdRequest extends ContentResourceRequest<PermissionsJson, AccessControlList> {

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

            return this.send().then((response: api.rest.JsonResponse<PermissionsJson>) => {
                return AccessControlList.fromJson(response.getResult());
            });
        }
    }
}
