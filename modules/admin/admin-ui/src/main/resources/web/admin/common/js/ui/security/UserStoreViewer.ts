module api.ui.security {

    export class UserStoreViewer extends api.ui.NamesAndIconViewer<api.security.UserStore> {

        private removeClickedListeners: {(event: MouseEvent):void}[] = [];

        constructor() {
            super();
        }

        resolveDisplayName(object: api.security.UserStore): string {
            return object.getDisplayName();
        }

        resolveSubName(object: api.security.UserStore, relativePath: boolean = false): string {
            return object.getKey().toString();
        }

        resolveIconClass(object: api.security.UserStore): string {
            return "icon-shield";
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