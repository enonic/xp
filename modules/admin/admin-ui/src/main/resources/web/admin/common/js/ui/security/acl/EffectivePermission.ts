module api.ui.security.acl {

    export class EffectivePermission {

        private access: Access;

        private permissionAccess: EffectivePermissionAccess;

        public getAccess(): Access {
            return this.access;
        }

        public getMembers(): EffectivePermissionMember[] {
            return this.permissionAccess.getUsers();
        }

        static fromJson(json: api.content.json.EffectivePermissionJson) {

            var effectivePermission = new EffectivePermission();

            effectivePermission.access = Access[json.access];
            effectivePermission.permissionAccess = EffectivePermissionAccess.fromJson(json.permissionAccessJson);

            return effectivePermission;
        }


    }

}
