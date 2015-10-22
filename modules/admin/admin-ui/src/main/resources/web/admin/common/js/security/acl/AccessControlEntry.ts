module api.security.acl {

    import ArrayHelper = api.util.ArrayHelper;
    import Principal = api.security.Principal;

    export class AccessControlEntry implements api.Equitable, api.Cloneable {

        private static ALL_PERMISSIONS: Permission[] = [Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE,
            Permission.PUBLISH,
            Permission.READ_PERMISSIONS, Permission.WRITE_PERMISSIONS
        ];

        private principal: Principal;

        private allowedPermissions: Permission[];

        private deniedPermissions: Permission[];

        constructor(principal: Principal) {
            this.principal = api.util.assertNotNull(principal);
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

        getPrincipalTypeName(): string {
            return this.principal.getTypeName();
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

        getAccess(): Access {
            if (this.deniedPermissions.length == 0) {
                if (this.isFullAccess(this.allowedPermissions)) {
                    return Access.FULL;
                } else if (this.isCanPublish(this.allowedPermissions)) {
                    return Access.PUBLISH;
                } else if (this.isCanWrite(this.allowedPermissions)) {
                    return Access.WRITE;
                } else if (this.isCanRead(this.allowedPermissions)) {
                    return Access.READ;
                }
            }
            return Access.CUSTOM;
        }

        private isCanRead(allowed: Permission[]): boolean {
            return allowed.indexOf(Permission.READ) >= 0 && allowed.length === 1;
        }

        private isCanWrite(allowed: Permission[]): boolean {
            return allowed.indexOf(Permission.READ) >= 0 &&
                   allowed.indexOf(Permission.CREATE) >= 0 &&
                   allowed.indexOf(Permission.MODIFY) >= 0 &&
                   allowed.indexOf(Permission.DELETE) >= 0 && allowed.length === 4;
        }

        private isCanPublish(allowed: Permission[]): boolean {
            return allowed.indexOf(Permission.READ) >= 0 &&
                   allowed.indexOf(Permission.CREATE) >= 0 &&
                   allowed.indexOf(Permission.MODIFY) >= 0 &&
                   allowed.indexOf(Permission.DELETE) >= 0 &&
                   allowed.indexOf(Permission.PUBLISH) >= 0 && allowed.length === 5;
        }

        private isFullAccess(allowed: Permission[]): boolean {
            return allowed.indexOf(Permission.READ) >= 0 &&
                   allowed.indexOf(Permission.CREATE) >= 0 &&
                   allowed.indexOf(Permission.MODIFY) >= 0 &&
                   allowed.indexOf(Permission.DELETE) >= 0 &&
                   allowed.indexOf(Permission.PUBLISH) >= 0 &&
                   allowed.indexOf(Permission.READ_PERMISSIONS) >= 0 &&
                   allowed.indexOf(Permission.WRITE_PERMISSIONS) >= 0 && allowed.length === 7;
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

            if (!api.ObjectHelper.equals(this.getPrincipalKey(), other.getPrincipalKey())) {
                return false;
            }

            if (!api.ObjectHelper.anyArrayEquals(this.allowedPermissions, other.allowedPermissions)) {
                return false;
            }

            if (!api.ObjectHelper.anyArrayEquals(this.deniedPermissions, other.deniedPermissions)) {
                return false;
            }
            return true;
        }

        toString(): string {
            var values = '';
            AccessControlEntry.ALL_PERMISSIONS.forEach((permission: Permission) => {
                if (this.isSet(permission)) {
                    if (values !== '') {
                        values += ', ';
                    }
                    values += this.isAllowed(permission) ? '+' : '-';
                    values += Permission[permission].toUpperCase();
                }
            });
            return this.getPrincipalKey().toString() + '[' + values + ']';
        }

        clone(): AccessControlEntry {
            var ace = new AccessControlEntry(this.principal.clone());
            ace.allowedPermissions = this.allowedPermissions.slice(0);
            ace.deniedPermissions = this.deniedPermissions.slice(0);
            return ace;
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
