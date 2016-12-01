module api.security {

    export class RoleKeys {

        public static EVERYONE: PrincipalKey = PrincipalKey.ofRole('system.everyone');

        public static AUTHENTICATED: PrincipalKey = PrincipalKey.ofRole('system.authenticated');

        public static ADMIN: PrincipalKey = PrincipalKey.ofRole('system.admin');

        public static CMS_ADMIN: PrincipalKey = PrincipalKey.ofRole('cms.admin');
    }
}
