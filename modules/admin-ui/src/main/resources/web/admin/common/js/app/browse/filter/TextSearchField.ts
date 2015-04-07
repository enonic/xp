module api.app.browse.filter {

    export class TextSearchField extends api.dom.InputEl {

        private timerId: number;

        private valueChangedListeners: Function[] = [];

        private previousValue: string;

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
            this.previousValue = '';
            if (!supressEvent) {
                this.notifyValueChanged();
            }
        }

        onValueChanged(listener: () => void) {
            this.valueChangedListeners.push(listener);
        }

        private notifyValueChanged() {
            debugger;
            var currentValue = this.getHTMLElement()['value'];
            if (currentValue == this.previousValue) {
                return;
            }
            this.valueChangedListeners.forEach((listener: () => void) => {
                listener();
            });
            this.previousValue = currentValue;
        }
    }
}