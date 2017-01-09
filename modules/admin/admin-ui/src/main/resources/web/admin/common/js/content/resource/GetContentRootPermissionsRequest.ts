module api.content.resource {

    import AccessControlList = api.security.acl.AccessControlList;
    import PermissionsJson = api.content.json.PermissionsJson;

    export class GetContentRootPermissionsRequest extends ContentResourceRequest<PermissionsJson, AccessControlList> {

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

            return this.send().then((response: api.rest.JsonResponse<PermissionsJson>) => {
                return AccessControlList.fromJson(response.getResult());
            });
        }
    }
}