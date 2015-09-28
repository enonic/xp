module api.ui.security.acl {

    import PrincipalType = api.security.PrincipalType;
    import AccessControlEntry = api.security.acl.AccessControlEntry;

    export class AccessControlEntryViewer extends api.ui.NamesAndIconViewer<AccessControlEntry> {

        constructor() {
            super();
        }

        resolveDisplayName(object: AccessControlEntry): string {
            return object.getPrincipalDisplayName();
        }

        resolveUnnamedDisplayName(object: AccessControlEntry): string {
            return object.getPrincipalTypeName();
        }

        resolveSubName(object: AccessControlEntry, relativePath: boolean = false): string {
            return object.getPrincipalKey().toPath();
        }

        resolveIconClass(object: AccessControlEntry): string {
            switch (object.getPrincipalKey().getType()) {
                case PrincipalType.USER:
                    return "icon-user";
                case PrincipalType.GROUP:
                    return "icon-users";
                case PrincipalType.ROLE:
                    return "icon-shield";
            }

            return "";
        }
    }

}