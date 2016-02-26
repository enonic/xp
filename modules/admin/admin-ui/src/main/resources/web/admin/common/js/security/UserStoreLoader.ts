module api.security {

    export class UserStoreLoader extends api.util.loader.BaseLoader<UserStoreListResult, UserStore> {

        constructor() {
            super(new ListUserStoresRequest());
        }
    }
}