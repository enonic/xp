module api.ui.button {

    export class TogglerButton extends api.ui.button.Button {

        private activeListeners: {(isActive: boolean): void}[] = [];

        constructor(className?: string, title?: string) {
            super();
            this.addClass('toggle-button icon-medium');
            if (className) {
                this.addClass(className);
            }
            this.setActive(false);
            this.setEnabled(false);

            if (title) {
                this.setTitle(title);

                this.onActiveChanged((isActive: boolean) => {
                    this.setTitle(isActive ? '' : title, true);
                });
            }

            this.onClicked((event: MouseEvent) => {
                if (this.isEnabled()) {
                    this.setActive(!this.isActive());
                }
            });
        }

        setActive(value: boolean) {
            this.toggleClass('active', value);
            this.notifyActiveChanged(value);
        }

        setVisible(value: boolean): TogglerButton {
            if (!value) {
                this.setActive(value);
            }
            return <TogglerButton>super.setVisible(value);
        }

        isActive() {
            return this.hasClass('active');
        }

        onActiveChanged(listener: (isActive: boolean) => void) {
            this.activeListeners.push(listener);
        }

        unActiveChanged(listener: (isActive: boolean) => void) {
            this.activeListeners = this.activeListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyActiveChanged(isActive: boolean) {
            this.activeListeners.forEach((listener) => {
                listener(isActive);
            });
        }
    }
}
