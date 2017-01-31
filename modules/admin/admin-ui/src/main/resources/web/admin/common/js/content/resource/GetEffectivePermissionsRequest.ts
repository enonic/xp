module api.content.resource {

    import AccessControlList = api.security.acl.AccessControlList;
    import EffectivePermissionJson = api.content.json.EffectivePermissionJson;
    import EffectivePermission = api.ui.security.acl.EffectivePermission;

    export class GetEffectivePermissionsRequest extends ContentResourceRequest<EffectivePermissionJson[], EffectivePermission[]> {

        private contentId: ContentId;

        constructor(contentId: ContentId) {
            super();
            super.setMethod('GET');
            this.contentId = contentId;
        }

        getParams(): Object {
            return {
                id: this.contentId.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'effectivePermissions');
        }

        sendAndParse(): wemQ.Promise<api.ui.security.acl.EffectivePermission[]> {

            return this.send().then((response: api.rest.JsonResponse<api.content.json.EffectivePermissionJson[]>) => {
                if (response.getJson()) {
                    return response.getJson().map((json) => {
                        return api.ui.security.acl.EffectivePermission.fromJson(json);
                    });
                }
                return null;
            });
        }
    }
}
