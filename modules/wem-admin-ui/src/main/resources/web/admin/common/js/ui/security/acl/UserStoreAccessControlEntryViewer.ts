module api.ui.security.acl {

    import PrincipalType = api.security.PrincipalType;
    import PrincipalKey = api.security.PrincipalKey;
    import UserStoreKey = api.security.UserStoreKey;
    import UserStoreAccessControlEntry = api.security.acl.UserStoreAccessControlEntry;

    export class UserStoreAccessControlEntryViewer extends api.ui.Viewer<UserStoreAccessControlEntry> {
        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().
                setSize(api.app.NamesAndIconViewSize.small).
                build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(ace: UserStoreAccessControlEntry) {
            super.setObject(ace);

            this.namesAndIconView.setMainName(ace.getPrincipalDisplayName()).
                setSubName(this.resolveSubName(ace.getPrincipalKey())).
                setIconClass(this.resolveIconClass(ace.getPrincipalKey()));
        }

        private resolveSubName(key: PrincipalKey): string {
            return key.toPath();
        }

        private resolveIconClass(key: PrincipalKey): string {
            return "icon-users";
        }

        getPreferredHeight(): number {
            return 50;
        }
    }

}