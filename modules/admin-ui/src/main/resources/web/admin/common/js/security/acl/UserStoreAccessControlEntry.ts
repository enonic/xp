module api.security.acl {

    export class UserStoreAccessControlEntry implements api.Equitable {

        private principal: Principal;

        private access: UserStoreAccess;

        constructor(principal: Principal, access?: UserStoreAccess) {
            api.util.assertNotNull(principal, "principal not set");
            //    api.util.assertNotNull(access, "access not set");
            this.principal = principal;
            this.access = access;
        }

        getPrincipal(): Principal {
            return this.principal;
        }

        getAccess(): UserStoreAccess {
            return this.access;
        }

        setAccess(value: string): UserStoreAccessControlEntry {
            this.access = UserStoreAccess[value];
            return this;
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

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, UserStoreAccessControlEntry)) {
                return false;
            }
            var other = <UserStoreAccessControlEntry>o;
            return this.principal.equals(other.getPrincipal()) &&
                   this.access === other.access;
        }

        getId(): string {
            return this.principal.getKey().toString();
        }

        toString(): string {
            return this.principal.getKey().toString() + '[' + UserStoreAccess[this.access] + ']';
        }

        toJson(): api.security.acl.UserStoreAccessControlEntryJson {
            return {
                "principal": this.principal.toJson(),
                "access": UserStoreAccess[this.access]
            };
        }

        static fromJson(json: api.security.acl.UserStoreAccessControlEntryJson): UserStoreAccessControlEntry {
            return new UserStoreAccessControlEntry(Principal.fromJson(json.principal), UserStoreAccess[json.access.toUpperCase()]);
        }
    }

}
