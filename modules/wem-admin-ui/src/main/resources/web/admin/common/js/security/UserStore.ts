module api.security {


    export class UserStore {
        private displayName: string;
        private key: UserStoreKey;

        constructor(builder: UserStoreBuilder) {
            this.displayName = builder.displayName;
            this.key = builder.key;
        }

        getDisplayName(): string {
            return this.displayName;
        }

        getKey(): UserStoreKey {
            return this.key;
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

        constructor() {
        }

        fromJson(json: api.security.UserStoreJson): UserStoreBuilder {
            this.key = new UserStoreKey(json.key);
            this.displayName = json.displayName;
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

        build(): UserStore {
            return new UserStore(this);
        }
    }
}