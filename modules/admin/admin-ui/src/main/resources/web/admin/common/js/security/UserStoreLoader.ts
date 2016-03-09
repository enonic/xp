module api.security {

    export class UserStoreLoader extends api.util.loader.BaseLoader<UserStoreListResult, UserStore> {

        constructor() {
            super(new ListUserStoresRequest());
        }

        filterFn(userstore: UserStore) {
            return userstore.getDisplayName().toString().toLowerCase().indexOf(this.getSearchString().toLowerCase()) != -1;
        }
    }
}