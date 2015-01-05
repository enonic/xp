module api.security {

    export class RoleKeys {

        public static OWNER: PrincipalKey = PrincipalKey.ofRole('owner');

        public static EVERYONE: PrincipalKey = PrincipalKey.ofRole('everyone');

    }
}
