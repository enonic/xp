module api.security {

    export class GetDefaultUserStoreRequest extends SecurityResourceRequest<UserStoreJson, UserStore> {

        constructor() {
            super();
            super.setMethod("GET");
        }

        getParams(): Object {
            return null;
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'userstore/default');
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