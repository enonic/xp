module api.content.resource {

    import AccessControlList = api.security.acl.AccessControlList;
    export class GetContentRootPermissionsRequest extends ContentResourceRequest<json.PermissionsJson, AccessControlList> {

        constructor() {
            super();
            super.setMethod("GET");
        }

        getParams(): Object {
            return {};
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "rootPermissions");
        }

        sendAndParse(): wemQ.Promise<AccessControlList> {

            return this.send().then((response: api.rest.JsonResponse<json.PermissionsJson>) => {
                return AccessControlList.fromJson(response.getResult());
            });
        }
    }
}