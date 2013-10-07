module api_app_browse_filter {

    export class TextSearchField extends api_dom.InputEl {

        private timerId:number;

        private valueChangedListeners:Function[] = [];

        constructor(placeholder?:string) {
            super('TextSearchField', 'text-search-field');
            this.setPlaceholder(placeholder);

            this.getEl().addEventListener('keydown', (event:any) => {
                if (event.which === 97) {
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

        clear(supressEvent?:boolean) {
            window.clearTimeout(this.timerId);
            this.getHTMLElement()['value'] = '';
            if (!supressEvent) {
                this.notifyValueChanged();
            }
        }

        setPlaceholder(placeholder:string) {
            this.getEl().setAttribute('placeholder', placeholder);
        }

        addValueChangedListener(listener:() => void) {
            this.valueChangedListeners.push(listener);
        }

        private notifyValueChanged() {
            this.valueChangedListeners.forEach((listener:() => void) => {
                listener();
            });
        }
    }
}