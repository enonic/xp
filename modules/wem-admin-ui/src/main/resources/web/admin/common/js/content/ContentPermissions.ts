module api.content {

    export class ContentPermissions {

        private permissions: api.security.acl.AccessControlList;

        private inheritedPermissions: api.security.acl.AccessControlList;

        constructor(builder: ContentPermissionsBuilder) {
            this.permissions = builder.permissions;
            this.inheritedPermissions = builder.inheritedPermissions;
        }


        static create(): ContentPermissionsBuilder {
            return new ContentPermissionsBuilder();
        }

        static fromJson(contentPermissionsJson: api.content.json.ContentPermissionsJson): ContentPermissions {
            return new ContentPermissionsBuilder().fromJson(contentPermissionsJson).build();
        }

    }

    export class ContentPermissionsBuilder {

        permissions: api.security.acl.AccessControlList;

        inheritedPermissions: api.security.acl.AccessControlList;

        constructor() {
            this.permissions = new api.security.acl.AccessControlList();
            this.inheritedPermissions = new api.security.acl.AccessControlList();
        }

        fromJson(contentPermissionsJson: api.content.json.ContentPermissionsJson): ContentPermissionsBuilder {
            this.permissions = api.security.acl.AccessControlList.fromJson(contentPermissionsJson.permissions);
            this.inheritedPermissions = api.security.acl.AccessControlList.fromJson(contentPermissionsJson.inheritedPermissions);
            return this;
        }

        setPermissions(acl: api.security.acl.AccessControlList): ContentPermissionsBuilder {
            this.permissions = acl;
            return this;
        }

        setInheritedPermissions(acl: api.security.acl.AccessControlList): ContentPermissionsBuilder {
            this.inheritedPermissions = acl;
            return this;
        }

        build(): ContentPermissions {
            return new ContentPermissions(this);
        }
    }
}