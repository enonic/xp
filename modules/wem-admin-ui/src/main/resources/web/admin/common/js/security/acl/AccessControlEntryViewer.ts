module api.security.acl {

    import PrincipalType = api.security.PrincipalType;
    import PrincipalKey = api.security.PrincipalKey;
    import UserStoreKey = api.security.UserStoreKey;

    export class AccessControlEntryViewer extends api.ui.Viewer<AccessControlEntry> {
        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().
                setSize(api.app.NamesAndIconViewSize.small).
                build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(ace: AccessControlEntry) {
            super.setObject(ace);

            this.namesAndIconView.setMainName(ace.getPrincipalDisplayName()).
                setSubName(this.resolveSubName(ace.getPrincipalKey())).
                setIconClass(this.resolveIconClass(ace.getPrincipalKey()));
        }

        private resolveSubName(key: PrincipalKey): string {
            return key.toPath();
        }

        private resolveIconClass(key: PrincipalKey): string {
            var iconClass;
            switch (key.getType()) {
            case PrincipalType.USER:
                iconClass = "icon-user";
                break;
            case PrincipalType.GROUP:
                iconClass = "icon-users";
                break;
            case PrincipalType.ROLE:
                iconClass = "icon-user7";
                break;
            }
            return iconClass;
        }

        getPreferredHeight(): number {
            return 50;
        }
    }

}