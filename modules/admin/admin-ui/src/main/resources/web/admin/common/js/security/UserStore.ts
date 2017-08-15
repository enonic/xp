module api.security {

    export class UserStore extends UserItem {

        private authConfig: AuthConfig;

        private idProviderMode: IdProviderMode;

        private permissions: api.security.acl.UserStoreAccessControlList;

        constructor(builder: UserStoreBuilder) {
            super(builder);
            this.authConfig = builder.authConfig;
            this.idProviderMode = builder.idProviderMode;
            this.permissions = builder.permissions || new api.security.acl.UserStoreAccessControlList();
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
            new GetPrincipalsByUserStoreRequest(<UserStoreKey>this.getKey(),
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

        getKey(): UserStoreKey {
            return <UserStoreKey>super.getKey();
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, UserStore)) {
                return false;
            }

            let other = <UserStore> o;

            return super.equals(other) &&
                   ((!this.authConfig && !other.authConfig) || (this.authConfig && this.authConfig.equals(other.authConfig))) &&
                   this.permissions.equals(other.permissions);
        }

        clone(): UserStore {
            return this.newBuilder().build();
        }

        newBuilder(): UserStoreBuilder {
            return new UserStoreBuilder(this);
        }

        static create(): UserStoreBuilder {
            return new UserStoreBuilder();
        }

        static fromJson(json: api.security.UserStoreJson): UserStore {
            return new UserStoreBuilder().fromJson(json).build();
        }
    }

    export class UserStoreBuilder extends UserItemBuilder {

        authConfig: AuthConfig;

        idProviderMode: IdProviderMode;

        permissions: api.security.acl.UserStoreAccessControlList;

        constructor(source?: UserStore) {
            if (source) {
                super(source);
                this.idProviderMode = source.getIdProviderMode();
                this.authConfig = !!source.getAuthConfig() ? source.getAuthConfig().clone() : null;
                this.permissions = !!source.getPermissions() ? source.getPermissions().clone() : null;
            }
        }

        fromJson(json: api.security.UserStoreJson): UserStoreBuilder {
            super.fromJson(json);
            this.key = new UserStoreKey(json.key);
            this.authConfig = json.authConfig ? AuthConfig.fromJson(json.authConfig) : null;
            this.idProviderMode = json.idProviderMode ? IdProviderMode[json.idProviderMode] : null;
            this.permissions = json.permissions ? api.security.acl.UserStoreAccessControlList.fromJson(json.permissions) : null;
            return this;
        }

        setKey(key: string): UserStoreBuilder {
            this.key = UserStoreKey.fromString(key);
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

        setDisplayName(displayName: string): UserStoreBuilder {
            super.setDisplayName(displayName);
            return this;
        }

        setDescription(description: string): UserStoreBuilder {
            super.setDescription(description);
            return this;
        }

        build(): UserStore {
            return new UserStore(this);
        }
    }
}
