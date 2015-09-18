module api.ui.security.acl {

    import UserStoreAccessControlEntry = api.security.acl.UserStoreAccessControlEntry;

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
            return "icon-users";
        }
    }

}