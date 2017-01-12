module api.ui.security.acl {

    import UserStoreAccessControlEntry = api.security.acl.UserStoreAccessControlEntry;
    import PrincipalType = api.security.PrincipalType;

    export class UserStoreAccessControlEntryViewer extends api.ui.NamesAndIconViewer<UserStoreAccessControlEntry> {

        constructor() {
            super();
        }

        resolveDisplayName(object: UserStoreAccessControlEntry): string {
            return object.getPrincipalDisplayName();
        }

        resolveUnnamedDisplayName(object: UserStoreAccessControlEntry): string {
            return object.getPrincipalTypeName();
        }

        resolveSubName(object: UserStoreAccessControlEntry, relativePath: boolean = false): string {
            return object.getPrincipalKey().toPath();
        }

        resolveIconClass(object: UserStoreAccessControlEntry): string {
            switch (object.getPrincipal().getKey().getType()) {
            case PrincipalType.USER:
                return "icon-user";
            case PrincipalType.GROUP:
                return "icon-users";
            case PrincipalType.ROLE:
                return "icon-masks";
            }

            return "";
        }
    }

}
