module api.security {
    import UserStoreJson = api.security.UserStoreJson;
    export class UserStoreListResult {

        userStores: UserStoreJson[];
    }
}