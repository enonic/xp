module app.wizard.page.contextwindow {

    export class ContextWindowToggler extends api.ui.button.Button {

        private activeListeners: {(isActive: boolean): void}[] = [];

        constructor() {
            super();
            this.addClass("toggle-button icon-cog icon-medium");
            this.setActive(false);
            this.setEnabled(false);

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
    }
}