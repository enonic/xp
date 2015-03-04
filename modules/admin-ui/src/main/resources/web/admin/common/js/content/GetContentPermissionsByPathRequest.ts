module api.content {

    import AccessControlList = api.security.acl.AccessControlList;

    export class GetContentPermissionsByPathRequest extends ContentResourceRequest<json.ContentPermissionsJson, AccessControlList> {

        private contentPath:ContentPath;

        constructor(path:ContentPath) {
            super();
            super.setMethod("GET");
            this.contentPath = path;
        }

        getParams():Object {
            return {
                path: this.contentPath.toString()
            };
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "contentPermissions");
        }

        sendAndParse(): wemQ.Promise<AccessControlList> {

            return this.send().then((response: api.rest.JsonResponse<json.ContentPermissionsJson>) => {
                return AccessControlList.fromJson(response.getResult().permissions);
            });
        }
    }
}