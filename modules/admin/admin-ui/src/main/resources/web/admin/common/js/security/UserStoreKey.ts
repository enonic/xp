module api.security {

    export class UserStoreKey extends UserItemKey {

        public static SYSTEM: UserStoreKey = new UserStoreKey('system');

        constructor(id: string) {
            super(id);
        }

        isSystem(): boolean {
            return this.getId() === UserStoreKey.SYSTEM.getId();
        }

        static fromString(value: string): UserStoreKey {
            return new UserStoreKey(value);
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, UserStoreKey)) {
                return false;
            }

            return super.equals(o);
        }
    }
}
