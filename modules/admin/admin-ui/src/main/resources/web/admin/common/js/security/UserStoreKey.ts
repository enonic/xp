module api.security {

    export class UserStoreKey implements api.Equitable {

        public static SYSTEM: UserStoreKey = new UserStoreKey('system');

        private id: string;

        constructor(id: string) {
            api.util.assert(!api.util.StringHelper.isBlank(id), "UserStoreKey id cannot be null or empty");
            this.id = id;
        }

        isSystem(): boolean {
            return this.id === UserStoreKey.SYSTEM.id;
        }

        toString(): string {
            return this.id;
        }

        getId(): string {
            return this.id;
        }

        static fromString(value: string): UserStoreKey {
            return new UserStoreKey(value);
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, UserStoreKey)) {
                return false;
            }

            var other = <UserStoreKey>o;
            return this.id === other.id;
        }
    }
}