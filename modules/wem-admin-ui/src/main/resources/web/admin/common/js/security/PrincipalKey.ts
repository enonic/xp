module api.security {

    export class PrincipalKey {

        private static SEPARATOR = ":";

        private static ANONYMOUS_PRINCIPAL: PrincipalKey = new PrincipalKey(UserStoreKey.SYSTEM, PrincipalType.USER, "anonymous");

        private userStore: UserStoreKey;

        private type: PrincipalType;

        private principalId: string;

        private refString: string;

        public static fromString(str: string): PrincipalKey {
            if (str === PrincipalKey.ANONYMOUS_PRINCIPAL.refString) {
                return PrincipalKey.ANONYMOUS_PRINCIPAL;
            }

            var sepIndex: number = str.indexOf(PrincipalKey.SEPARATOR);
            if (sepIndex == -1) {
                throw new Error("Not a valid principal key [" + str + "]");
            }
            var sepIndex2: number = str.indexOf(PrincipalKey.SEPARATOR, sepIndex + 1);
            if (sepIndex2 == -1) {
                throw new Error("Not a valid principal key [" + str + "]");
            }
            var userStore = str.substring(0, sepIndex);
            var typeStr = str.substring(sepIndex + 1, sepIndex2) || '';
            var type: PrincipalType = PrincipalType[typeStr.toUpperCase()];
            var principalId = str.substring(sepIndex2 + 1, str.length);

            return new PrincipalKey(new UserStoreKey(userStore), type, principalId);
        }

        constructor(userStore: UserStoreKey, type: PrincipalType, principalId: string) {
            api.util.assertNotNull(userStore, "Principal user store cannot be null");
            api.util.assertNotNull(type, "Principal type cannot be null");
            api.util.assert(!api.util.StringHelper.isBlank(principalId), "Principal id cannot be null or empty");
            this.userStore = userStore;
            this.type = type;
            this.principalId = principalId;
            this.refString =
            userStore.toString() + PrincipalKey.SEPARATOR + PrincipalType[type].toLowerCase() + PrincipalKey.SEPARATOR + principalId;
        }

        getUserStore(): UserStoreKey {
            return this.userStore;
        }

        getType(): PrincipalType {
            return this.type;
        }

        getId(): string {
            return this.principalId;
        }

        isUser(): boolean {
            return this.type === PrincipalType.USER;
        }

        isGroup(): boolean {
            return this.type === PrincipalType.GROUP;
        }

        isRole(): boolean {
            return this.type === PrincipalType.ROLE;
        }

        isAnonymous(): boolean {
            return this.refString === PrincipalKey.ANONYMOUS_PRINCIPAL.refString;
        }

        toString(): string {
            return this.refString;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, PrincipalKey)) {
                return false;
            }

            var other = <PrincipalKey>o;
            if (!api.ObjectHelper.stringEquals(this.refString, other.refString)) {
                return false;
            }
            return true;
        }

        public static ofAnonymous(): PrincipalKey {
            return PrincipalKey.ANONYMOUS_PRINCIPAL;
        }

        public static ofUser(userStore: UserStoreKey, userId: string): PrincipalKey {
            return new PrincipalKey(userStore, PrincipalType.USER, userId);
        }

        public static ofGroup(userStore: UserStoreKey, groupId: string): PrincipalKey {
            return new PrincipalKey(userStore, PrincipalType.GROUP, groupId);
        }

        public static ofRole(roleId: string): PrincipalKey {
            return new PrincipalKey(UserStoreKey.SYSTEM, PrincipalType.ROLE, roleId);
        }
    }
}
