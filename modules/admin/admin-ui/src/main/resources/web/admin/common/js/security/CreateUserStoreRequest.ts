module api.security {

    export class CreateUserStoreRequest extends SecurityResourceRequest<UserStoreJson, UserStore> {

        private userStoreKey: UserStoreKey;
        private displayName: string;
        private description: string;
        private authConfig: AuthConfig;
        private permissions: api.security.acl.UserStoreAccessControlList;

        constructor() {
            super();
            super.setMethod("POST");
        }

        getParams(): Object {
            return {
                key: this.userStoreKey.toString(),
                displayName: this.displayName,
                description: this.description,
                authConfig: this.authConfig ? this.authConfig.toJson() : undefined,
                permissions: this.permissions ? this.permissions.toJson() : []
            };
        }

        setKey(userStoreKey: UserStoreKey): CreateUserStoreRequest {
            this.userStoreKey = userStoreKey;
            return this;
        }

        setDisplayName(displayName: string): CreateUserStoreRequest {
            this.displayName = displayName;
            return this;
        }

        setDescription(description: string): CreateUserStoreRequest {
            this.description = description;
            return this;
        }

        setAuthConfig(authConfig: AuthConfig): CreateUserStoreRequest {
            this.authConfig = authConfig;
            return this;
        }

        setPermissions(permissions: api.security.acl.UserStoreAccessControlList): CreateUserStoreRequest {
            this.permissions = permissions;
            return this;
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'userstore/create');
        }

        sendAndParse(): wemQ.Promise<UserStore> {
            return this.send().then((response: api.rest.JsonResponse<UserStoreJson>) => {
                return this.fromJsonToUserStore(response.getResult());
            });
        }

        fromJsonToUserStore(json: UserStoreJson): UserStore {
            return UserStore.fromJson(json);
        }
    }
}
