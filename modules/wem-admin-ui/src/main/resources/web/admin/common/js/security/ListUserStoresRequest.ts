module api.security {

    export class ListUserStoresRequest extends SecurityResourceRequest<UserStoreListResult, UserStore[]> {

        constructor() {
            super();
            super.setMethod("GET");
        }

        getParams(): Object {
            return {};
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'userstore/list');
        }

        sendAndParse(): wemQ.Promise<UserStore[]> {

            return this.send().then((response: api.rest.JsonResponse<UserStoreListResult>) => {
                return response.getResult().userStores.map((userStoreJson: UserStoreJson) => {
                    return this.fromJsonToUserStore(userStoreJson);
                });
            });
        }

        fromJsonToUserStore(json: UserStoreJson): UserStore {
            return UserStore.fromJson(json);
        }
    }
}