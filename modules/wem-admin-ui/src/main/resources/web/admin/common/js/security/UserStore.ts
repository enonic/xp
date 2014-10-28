module api.security {

    export class UserStore {

        private displayName: string;
        private key: string;

        constructor(displayName: string, key: string) {
            this.displayName = displayName;
            this.key = key;
        }

        getDisplayName(): string {
            return this.displayName;
        }

        getKey(): string {
            return this.key;
        }
    }

}
