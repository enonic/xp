module api.security {

    export class UserStore {

        private displayName: string;
        private key: UserStoreKey;

        constructor(displayName: string, key: string) {
            this.displayName = displayName;
            this.key = new UserStoreKey(key);
        }

        getDisplayName(): string {
            return this.displayName;
        }

        getKey(): UserStoreKey {
            return this.key;
        }
    }

}
