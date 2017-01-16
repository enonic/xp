module api.security {

    export class UserStore implements api.Equitable {
        private displayName: string;
        private key: UserStoreKey;
        private description: string;
        private authConfig: AuthConfig;
        private idProviderMode: IdProviderMode;
        private permissions: api.security.acl.UserStoreAccessControlList;

        constructor(builder: UserStoreBuilder) {
            this.displayName = builder.displayName;
            this.key = builder.key;
            this.description = builder.description;
            this.authConfig = builder.authConfig;
            this.idProviderMode = builder.idProviderMode;
            this.permissions = builder.permissions || new api.security.acl.UserStoreAccessControlList();
        }

        getDisplayName(): string {
            return this.displayName;
        }

        getKey(): UserStoreKey {
            return this.key;
        }

        getDescription(): string {
            return this.description;
        }

        getAuthConfig(): AuthConfig {
            return this.authConfig;
        }

        getIdProviderMode(): IdProviderMode {
            return this.idProviderMode;
        }

        getPermissions(): api.security.acl.UserStoreAccessControlList {
            return this.permissions;
        }

        isDeletable(): wemQ.Promise<boolean> {
            let deferred = wemQ.defer<boolean>();
            new GetPrincipalsByUserStoreRequest(this.key,
                [PrincipalType.USER, PrincipalType.GROUP]).
                sendAndParse().then((principals: Principal[]) => {
                    if (principals.length > 0) {
                        deferred.resolve(false);
                    } else {
                        deferred.resolve(true);
                    }
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                    deferred.resolve(false);
                }).done();
            ;
            return deferred.promise;
        }

        static checkOnDeletable(key: UserStoreKey): wemQ.Promise<boolean> {
            return !!key ? UserStore.create().setKey(key.toString()).build().isDeletable() : null;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, UserStore)) {
                return false;
            }

            let other = <UserStore> o;

            return this.key.equals(other.key) &&
                   this.displayName === other.displayName &&
                   this.description === other.description &&
                   ((!this.authConfig && !other.authConfig) || (this.authConfig && this.authConfig.equals(other.authConfig))) &&
                   this.permissions.equals(other.permissions);
        }

        clone(): UserStore {
            return UserStore.create().
                setDisplayName(this.displayName).
                setKey(this.key.toString()).
                setDescription(this.description).
                setAuthConfig(this.authConfig ? this.authConfig.clone() : this.authConfig).
                setIdProviderMode(this.idProviderMode).
                setPermissions(this.permissions.clone()).
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
        description: string;
        authConfig: AuthConfig;
        idProviderMode: IdProviderMode;
        permissions: api.security.acl.UserStoreAccessControlList;

        fromJson(json: api.security.UserStoreJson): UserStoreBuilder {
            this.key = new UserStoreKey(json.key);
            this.displayName = json.displayName;
            this.description = json.description;
            this.authConfig = json.authConfig ? AuthConfig.fromJson(json.authConfig) : null;
            this.idProviderMode = json.idProviderMode ? IdProviderMode[json.idProviderMode] : null;
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

        setDescription(description: string): UserStoreBuilder {
            this.description = description;
            return this;
        }

        setAuthConfig(authConfig: AuthConfig): UserStoreBuilder {
            this.authConfig = authConfig;
            return this;
        }

        setIdProviderMode(idProviderMode: IdProviderMode): UserStoreBuilder {
            this.idProviderMode = idProviderMode;
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
