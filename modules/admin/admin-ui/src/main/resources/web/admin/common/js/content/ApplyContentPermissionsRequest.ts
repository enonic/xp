module api.content {

    export class ApplyContentPermissionsRequest extends ContentResourceRequest<api.content.json.ContentJson, Content> {

        private id: string;

        private permissions: api.security.acl.AccessControlList;

        private inheritPermissions: boolean;

        private overwriteChildPermissions: boolean;

        constructor() {
            super();
            this.inheritPermissions = true;
            this.overwriteChildPermissions = false;
            this.setMethod("POST");
        }

        setId(id: string): ApplyContentPermissionsRequest {
            this.id = id;
            return this;
        }

        setPermissions(permissions: api.security.acl.AccessControlList): ApplyContentPermissionsRequest {
            this.permissions = permissions;
            return this;
        }

        setInheritPermissions(inheritPermissions: boolean): ApplyContentPermissionsRequest {
            this.inheritPermissions = inheritPermissions;
            return this;
        }

        setOverwriteChildPermissions(overwriteChildPermissions: boolean): ApplyContentPermissionsRequest {
            this.overwriteChildPermissions = overwriteChildPermissions;
            return this;
        }

        getParams(): Object {
            return {
                contentId: this.id,
                permissions: this.permissions ? this.permissions.toJson() : undefined,
                inheritPermissions: this.inheritPermissions,
                overwriteChildPermissions: this.overwriteChildPermissions
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "applyPermissions");
        }

        sendAndParse(): wemQ.Promise<Content> {

            return this.send().then((response: api.rest.JsonResponse<api.content.json.ContentJson>) => {
                return this.fromJsonToContent(response.getResult());
            });
        }

    }

}