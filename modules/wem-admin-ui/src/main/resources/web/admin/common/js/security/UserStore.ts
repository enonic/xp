module api.security {

    export class UserStore extends UserTreeGridItem {

        private key: UserStoreKey;

        constructor(builder: UserStoreBuilder) {
            super(builder.displayName)
            this.key = builder.key;
        }


        getKey(): UserStoreKey {
            return this.key;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, UserStore)) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }

            var other = <UserStore> o;
            return this.key.equals(other.key);
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
