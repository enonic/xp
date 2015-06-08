module app.wizard.page.contextwindow {

    export class ContextWindowToggler extends api.ui.button.Button {

        private activeListeners: {(isActive: boolean): void}[] = [];
        private forcedListeners: {(isActive: boolean): void}[] = [];

        constructor() {
            super();
            this.addClass("toggle-button icon-menu3 icon-medium");
            this.setActive(false);

            this.onClicked((event: MouseEvent) => {
                if (this.isEnabled()) {
                    this.setActive(!this.isActive());
                }
            })
        }

        setActive(value: boolean, silent: boolean = false) {
            this.toggleClass('active', value);
            if (!silent) {
                this.notifyActiveChanged(value);
            }
        }

        isActive() {
            return this.hasClass("active");
        }

        setForced(value: boolean, silent: boolean = false) {
            this.toggleClass('forced', value);
            if (!silent) {
                this.notifyForcedChanged(value);
            }
        }

        isForced(): boolean {
            return this.hasClass('forced');
        }

        onActiveChanged(listener: (isActive: boolean) => void) {
            this.activeListeners.push(listener);
        }

        unActiveChanged(listener: (isActive: boolean) => void) {
            this.activeListeners = this.activeListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyActiveChanged(isActive: boolean) {
            this.activeListeners.forEach((listener) => {
                listener(isActive);
            });
        }

        onForcedChanged(listener: (forced: boolean) => void) {
            this.forcedListeners.push(listener);
        }

        unForcedChanged(listener: (forced: boolean) => void) {
            this.forcedListeners = this.forcedListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyForcedChanged(forced: boolean) {
            this.forcedListeners.forEach((listener) => {
                listener(forced);
            });
        }
    }
}