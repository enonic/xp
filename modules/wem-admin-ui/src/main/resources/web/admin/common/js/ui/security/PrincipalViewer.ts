module api.ui.security {

    import Principal = api.security.Principal;
    import PrincipalKey = api.security.PrincipalKey;
    import PrincipalType = api.security.PrincipalType;

    export class PrincipalViewer extends api.ui.Viewer<Principal> {

        private namesAndIconView: api.app.NamesAndIconView;

        private removeClickedListeners: {(event: MouseEvent):void}[] = [];

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
                iconClass = "icon-shield";
                break;
            }
            return iconClass;
        }

        getPreferredHeight(): number {
            return 50;
        }

        onRemoveClicked(listener: (event: MouseEvent) => void) {
            this.removeClickedListeners.push(listener);
        }

        unRemoveClicked(listener: (event: MouseEvent) => void) {
            this.removeClickedListeners = this.removeClickedListeners.filter((current) => {
                return current !== listener;
            })
        }

        notifyRemoveClicked(event: MouseEvent) {
            this.removeClickedListeners.forEach((listener) => {
                listener(event);
            })
        }
    }
}