module api.security {

    export class RoleKeys {

        private static ROLE_ADMIN: string = 'system.admin';

        private static ROLE_CMS_ADMIN: string = 'cms.admin';

        private static ROLE_USER_ADMIN: string = 'system.user.admin';

        private static ROLE_EVERYONE: string = 'system.everyone';

        private static ROLE_AUTHENTICATED: string = 'system.authenticated';

        private static ROLE_CMS_EXPERT: string = 'cms.expert';

        /* */

        public static EVERYONE: PrincipalKey = PrincipalKey.ofRole(RoleKeys.ROLE_EVERYONE);

        public static AUTHENTICATED: PrincipalKey = PrincipalKey.ofRole(RoleKeys.ROLE_AUTHENTICATED);

        public static ADMIN: PrincipalKey = PrincipalKey.ofRole(RoleKeys.ROLE_ADMIN);

        public static CMS_ADMIN: PrincipalKey = PrincipalKey.ofRole(RoleKeys.ROLE_CMS_ADMIN);

        public static USER_ADMIN: PrincipalKey = PrincipalKey.ofRole(RoleKeys.ROLE_USER_ADMIN);

        public static CMS_EXPERT: PrincipalKey = PrincipalKey.ofRole(RoleKeys.ROLE_CMS_EXPERT);

        /* */

        private static contentAdminRoles: string[] = [RoleKeys.ROLE_ADMIN, RoleKeys.ROLE_CMS_ADMIN];

        private static userAdminRoles: string[] = [RoleKeys.ROLE_ADMIN, RoleKeys.ROLE_USER_ADMIN];

        private static contentExpertRoles: string[] = [RoleKeys.ROLE_ADMIN, RoleKeys.ROLE_CMS_ADMIN, RoleKeys.ROLE_CMS_EXPERT];

        /* */

        public static isContentAdmin(principalKey: PrincipalKey): boolean {
            return RoleKeys.contentAdminRoles.some(roleId => principalKey.getId() === roleId);
        }

        public static isUserAdmin(principalKey: PrincipalKey): boolean {
            return RoleKeys.userAdminRoles.some(roleId => principalKey.getId() === roleId);
        }

        public static isContentExpert(principalKey: PrincipalKey): boolean {
            return RoleKeys.contentExpertRoles.some(roleId => principalKey.getId() === roleId);
        }

        public static isAdmin(principalKey: PrincipalKey): boolean {
            return RoleKeys.ROLE_ADMIN === principalKey.getId();
        }

        public static isEveryone(principalKey: PrincipalKey): boolean {
            return RoleKeys.ROLE_EVERYONE === principalKey.getId();
        }
    }
}
