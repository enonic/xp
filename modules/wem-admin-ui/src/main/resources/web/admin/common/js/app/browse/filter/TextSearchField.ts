module api.app.browse.filter {

    export class TextSearchField extends api.dom.InputEl {

        private timerId: number;

        private valueChangedListeners: Function[] = [];

        constructor(placeholder?: string) {
            super('text-search-field');
            this.setPlaceholder(placeholder);

            this.onKeyDown((event: KeyboardEvent) => {
                if (event.which == 9) {
                    // tab
                } else if (event.which == 13) {
                    // enter
                    this.notifyValueChanged();
                } else {
                    if (this.timerId !== null) {
                        window.clearTimeout(this.timerId);
                        this.timerId = null;
                    }
                    this.timerId = window.setTimeout(() => {
                        this.notifyValueChanged();
                    }, 500);
                }
            });
        }

        clear(supressEvent?: boolean) {
            window.clearTimeout(this.timerId);
            this.getHTMLElement()['value'] = '';
            if (!supressEvent) {
                this.notifyValueChanged();
            }
        }

        setPlaceholder(placeholder: string) {
            this.getEl().setAttribute('placeholder', placeholder);
        }

        onValueChanged(listener: () => void) {
            this.valueChangedListeners.push(listener);
        }

        private notifyValueChanged() {
            this.valueChangedListeners.forEach((listener: () => void) => {
                listener();
            });
        }
    }
}