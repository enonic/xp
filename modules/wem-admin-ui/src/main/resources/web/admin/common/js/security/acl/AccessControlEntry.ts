module api.security.acl {

    import ArrayHelper = api.util.ArrayHelper;
    import Principal = api.security.Principal;

    export class AccessControlEntry implements api.Equitable {

        private principal: Principal;

        private allowedPermissions: Permission[];

        private deniedPermissions: Permission[];

        constructor(principal: Principal) {
            this.principal = principal;
            this.allowedPermissions = [];
            this.deniedPermissions = [];
        }

        getPrincipal(): Principal {
            return this.principal;
        }

        getPrincipalKey(): PrincipalKey {
            return this.principal.getKey();
        }

        getPrincipalDisplayName(): string {
            return this.principal.getDisplayName();
        }

        getAllowedPermissions(): Permission[] {
            return this.allowedPermissions;
        }

        getDeniedPermissions(): Permission[] {
            return this.deniedPermissions;
        }

        setAllowedPermissions(permissions: Permission[]): void {
            this.allowedPermissions = permissions;
        }

        setDeniedPermissions(permissions: Permission[]): void {
            this.deniedPermissions = permissions;
        }

        isAllowed(permission: Permission): boolean {
            return (this.allowedPermissions.indexOf(permission) > -1) && (this.deniedPermissions.indexOf(permission) === -1);
        }

        isDenied(permission: Permission): boolean {
            return !this.isAllowed(permission);
        }

        isSet(permission: Permission): boolean {
            return (this.allowedPermissions.indexOf(permission) > -1) || (this.deniedPermissions.indexOf(permission) > -1);
        }

        allow(permission: Permission): AccessControlEntry {
            ArrayHelper.addUnique(permission, this.allowedPermissions);
            ArrayHelper.removeValue(permission, this.deniedPermissions);
            return this;
        }

        deny(permission: Permission): AccessControlEntry {
            ArrayHelper.addUnique(permission, this.deniedPermissions);
            ArrayHelper.removeValue(permission, this.allowedPermissions);
            return this;
        }

        remove(permission: Permission): AccessControlEntry {
            ArrayHelper.removeValue(permission, this.allowedPermissions);
            ArrayHelper.removeValue(permission, this.deniedPermissions);
            return this;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, AccessControlEntry)) {
                return false;
            }

            var other = <AccessControlEntry>o;
            return this.principal.getKey().equals(other.getPrincipalKey()) &&
                   (this.principal.getDisplayName() == other.getPrincipalDisplayName()) &&
                   this.permissionEquals(this.allowedPermissions, other.allowedPermissions) &&
                   this.permissionEquals(this.deniedPermissions, other.deniedPermissions);
        }

        private permissionEquals(listA: Permission[], listB: Permission[]): boolean {
            return (listA.length === listB.length) &&
                   listA.every(function (element, idx) {
                       return element === listB[idx];
                   });
        }

        toJson(): api.security.acl.AccessControlEntryJson {
            return {
                "principal": this.principal.toJson(),
                "allow": this.allowedPermissions.map((perm) => Permission[perm]),
                "deny": this.deniedPermissions.map((perm) => Permission[perm])
            };
        }

        static fromJson(json: api.security.acl.AccessControlEntryJson): AccessControlEntry {
            var ace = new AccessControlEntry(Principal.fromJson(json.principal));
            var allow: Permission[] = json.allow.map((permStr) => Permission[permStr.toUpperCase()]);
            var deny: Permission[] = json.deny.map((permStr) => Permission[permStr.toUpperCase()]);
            ace.setAllowedPermissions(allow);
            ace.setDeniedPermissions(deny);
            return ace;
        }
    }

}
