module api.ui.locale {

    import Locale = api.locale.Locale;

    export class LocaleViewer extends api.ui.Viewer<Locale> {

        private namesView: api.app.NamesView;

        private removeClickedListeners: {(event: MouseEvent):void}[] = [];

        constructor() {
            super();
            this.namesView = new api.app.NamesView();
            this.appendChild(this.namesView);
        }

        setObject(locale: Locale) {
            super.setObject(locale);

            this.namesView.setMainName(locale.getDisplayName());
        }

        getPreferredHeight(): number {
            return 30;
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