module api.security {

    export class UserStore implements api.Equitable {
        private displayName: string;
        private key: UserStoreKey;
        private permissions: api.security.acl.UserStoreAccessControlList;

        constructor(builder: UserStoreBuilder) {
            this.displayName = builder.displayName;
            this.key = builder.key;
            this.permissions = builder.permissions || new api.security.acl.UserStoreAccessControlList();
        }

        getDisplayName(): string {
            return this.displayName;
        }

        getKey(): UserStoreKey {
            return this.key;
        }

        getPermissions(): api.security.acl.UserStoreAccessControlList {
            return this.permissions;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, UserStore)) {
                return false;
            }

            var other = <UserStore> o;

            return this.key.equals(other.key) &&
                   this.displayName === other.displayName &&
                   this.permissions.equals(other.permissions)
        }

        clone(): UserStore {
            return UserStore.create().
                setDisplayName(this.displayName).
                setKey(this.key.toString()).
                setPermissions(this.permissions).
                build();
        }

        static create(): UserStoreBuilder {
            return new UserStoreBuilder();
        }

        static fromJson(json: api.security.UserStoreJson): UserStore {
            return new UserStoreBuilder().fromJson(json).build();
        }
    }

    export class UserStoreBuilder {
        displayName: string;
        key: UserStoreKey;
        permissions: api.security.acl.UserStoreAccessControlList;

        constructor() {
        }

        fromJson(json: api.security.UserStoreJson): UserStoreBuilder {
            this.key = new UserStoreKey(json.key);
            this.displayName = json.displayName;
            this.permissions = json.permissions ? api.security.acl.UserStoreAccessControlList.fromJson(json.permissions) : null;
            return this;
        }

        setKey(key: string): UserStoreBuilder {
            this.key = new UserStoreKey(key);
            return this;
        }

        setDisplayName(displayName: string): UserStoreBuilder {
            this.displayName = displayName;
            return this;
        }

        setPermissions(permissions: api.security.acl.UserStoreAccessControlList): UserStoreBuilder {
            this.permissions = permissions;
            return this;
        }

        build(): UserStore {
            return new UserStore(this);
        }
    }
}