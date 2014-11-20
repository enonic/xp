module api.ui.security {

    import Principal = api.security.Principal;
    import PrincipalType = api.security.PrincipalType;
    import PrincipalKey = api.security.PrincipalKey;

    export class PrincipalViewer extends api.ui.Viewer<Principal> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().
                setSize(api.app.NamesAndIconViewSize.small).
                build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(principal: Principal) {
            super.setObject(principal);

            this.namesAndIconView.setMainName(principal.getDisplayName()).
                setSubName(this.resolveSubName(principal.getKey())).
                setIconClass(this.resolveIconClass(principal.getKey()));
        }


        private resolveSubName(key: PrincipalKey): string {
            return api.util.StringHelper.format("{0}/{1}/{2}",
                key.getUserStore().toString(),
                PrincipalType[key.getType()].toLowerCase(),
                key.getId());
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