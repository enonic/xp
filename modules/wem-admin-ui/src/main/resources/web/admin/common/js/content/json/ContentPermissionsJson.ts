module api.content.json {

    export interface ContentPermissionsJson {

        permissions: api.security.acl.AccessControlEntryJson[];

        inheritedPermissions: api.security.acl.AccessControlEntryJson[];

    }

}