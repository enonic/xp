module api.security.acl {

    export class UserStoreAccessControlEntry implements api.Equitable {

        private principalKey: PrincipalKey;

        private access: UserStoreAccess;

        constructor(principalKey: PrincipalKey, access: UserStoreAccess) {
            api.util.assertNotNull(principalKey, "principalKey not set");
            api.util.assertNotNull(access, "access not set");
            this.principalKey = principalKey;
            this.access = access;
        }

        getPrincipalKey(): PrincipalKey {
            return this.principalKey;
        }

        getAccess(): UserStoreAccess {
            return this.access;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, UserStoreAccessControlEntry)) {
                return false;
            }
            var other = <UserStoreAccessControlEntry>o;
            return this.principalKey.equals(other.getPrincipalKey()) &&
                   this.access === other.access;
        }

        toString(): string {
            return this.getPrincipalKey().toString() + '[' + UserStoreAccess[this.access] + ']';
        }

        toJson(): api.security.acl.UserStoreAccessControlEntryJson {
            return {
                "principalKey": this.principalKey.toString(),
                "access": UserStoreAccess[this.access]
            };
        }

        static fromJson(json: api.security.acl.UserStoreAccessControlEntryJson): UserStoreAccessControlEntry {
            return new UserStoreAccessControlEntry(PrincipalKey.fromString(json.principalKey), UserStoreAccess[json.access.toUpperCase()]);
        }
    }

}
