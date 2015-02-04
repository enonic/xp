module api.ui.security {

    import Principal = api.security.Principal;
    import PrincipalType = api.security.PrincipalType;

    export class PrincipalViewer extends api.ui.NamesAndIconViewer<Principal> {

        private removeClickedListeners: {(event: MouseEvent):void}[] = [];

        constructor() {
            super();
        }

        resolveDisplayName(object: Principal): string {
            return object.getDisplayName();
        }

        resolveSubName(object: Principal, relativePath: boolean = false): string {
            return object.getKey().toPath();
        }

        resolveIconClass(object: Principal): string {
            switch (object.getKey().getType()) {
                case PrincipalType.USER:
                    return "icon-user";
                case PrincipalType.GROUP:
                    return "icon-users";
                case PrincipalType.ROLE:
                    return "icon-shield";
            }

            return "";
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