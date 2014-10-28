module api.security {

    export class UserStoreKey implements api.Equitable {

        public static SYSTEM: UserStoreKey = new UserStoreKey('system');

        private id: string;

        constructor(id: string) {
            this.id = id;
        }

        toString(): string {
            return this.id;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, UserStoreKey)) {
                return false;
            }

            var other = <UserStoreKey>o;
            if (!api.ObjectHelper.stringEquals(this.id, other.id)) {
                return false;
            }
            return true;
        }
    }
}