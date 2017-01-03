module api.security.acl {

    export class PermissionHelper {

        static hasPermission(permission: api.security.acl.Permission,
                             loginResult: api.security.auth.LoginResult,
                             accessControlList: AccessControlList): boolean {
            let result = false;
            let entries = accessControlList.getEntries();
            let accessEntriesWithGivenPermissions: AccessControlEntry[] = entries.filter((item: AccessControlEntry) => {
                return item.isAllowed(permission);
            });

            loginResult.getPrincipals().some((principalKey: api.security.PrincipalKey) => {
                if (api.security.RoleKeys.ADMIN.equals(principalKey) ||
                    this.isPrincipalPresent(principalKey, accessEntriesWithGivenPermissions)) {
                    result = true;
                    return true;
                }
            });
            return result;
        }

        static isPrincipalPresent(principalKey: api.security.PrincipalKey,
                                  accessEntriesToCheck: AccessControlEntry[]): boolean {
            let result = false;
            accessEntriesToCheck.some((entry: AccessControlEntry) => {
                if (entry.getPrincipalKey().equals(principalKey)) {
                    result = true;
                    return true;
                }
            });

            return result;
        }
    }

}
