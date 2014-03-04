module api.ui {

    export class TextInput extends api.dom.InputEl {

        static MIDDLE: string = 'middle';
        static LARGE: string = 'large';

        /**
         * Specifies RegExp for characters that will be removed during input.
         */
        private stripCharsRe: RegExp;
        /**
         * Forbidden chars filters out keyCodes for delete, backspace and arrow buttons in Firefox, so we need to
         * allow these to pass the filter (8=backspace, 46=delete, 39=right arrow, 47=left arrow)
         */
        private allowedKeyCodes: number[] = [8, 46, 39, 37];

        /**
         * Input value before it was changed by last input event.
         */
        private oldValue: string = "";

        private valueChangedListeners: {(event: ValueChangedEvent):void}[] = [];

        constructor(className?: string, size?: string) {
            super(className);

            this.getEl().setAttribute('type', 'text');
            this.addClass('text-input');
            if (size) {
                this.addClass(size);
            }

            this.getEl().addEventListener('keypress', (event: KeyboardEvent) => {
                if (!this.stripCharsRe) {
                    return;
                }

                var symbol = String.fromCharCode((<any> event).charCode);
                // prevent input of forbidden symbols
                if (this.containsForbiddenChars(symbol)) {
                    if (!this.keyCodeAllowed(event.keyCode)) {
                        event.preventDefault();
                        return false;
                    }

                }
            });

            this.getEl().addEventListener('input', () => {
                this.notifyValueChanged(this.oldValue, this.getValue());
                this.oldValue = this.getValue();
            });
        }

        static large(className?: string): TextInput {
            return new TextInput(className, TextInput.LARGE);
        }

        static middle(className?: string): TextInput {
            return new TextInput(className, TextInput.MIDDLE);
        }

        setValue(value: string): TextInput {
            var oldValue = this.getValue();
            var newValue = this.removeForbiddenChars(value);

            if (oldValue != newValue) {
                super.setValue(newValue);
                this.notifyValueChanged(oldValue, newValue);
                // save new value to know which value was before input event.
                this.oldValue = newValue;
            }

            return this;
        }

        setName(value: string): TextInput {
            super.setName(value);
            return this;
        }

        setPlaceholder(value: string): TextInput {
            this.getEl().setAttribute('placeholder', value);
            return this;
        }

        getPlaceholder(): string {
            return this.getEl().getAttribute('placeholder');
        }

        setForbiddenCharsRe(re: RegExp): TextInput {
            this.stripCharsRe = re;
            return this;
        }

        onValueChanged(listener: (event: ValueChangedEvent)=>void) {
            this.valueChangedListeners.push(listener);
        }

        unValueChanged(listener: (event: ValueChangedEvent)=>void) {
            this.valueChangedListeners = this.valueChangedListeners.filter((currentListener: (event: ValueChangedEvent)=>void) => {
                return listener != currentListener;
            });
        }

        private notifyValueChanged(oldValue: string, newValue: string) {
            this.valueChangedListeners.forEach((listener: (event: ValueChangedEvent)=>void) => {
                listener.call(this, new ValueChangedEvent(oldValue, newValue));
            });
        }

        private removeForbiddenChars(rawValue: string): string {
            return this.stripCharsRe ? (rawValue || '').replace(this.stripCharsRe, '') : rawValue;
        }

        private containsForbiddenChars(value: string): boolean {
            // create new RegExp object in order not to mess RegExp.lastIndex
            var forbidden = new RegExp(<any> this.stripCharsRe);
            return forbidden.test(value);
        }

        private keyCodeAllowed(keyCode: number): boolean {
            for (var i = 0; i < this.allowedKeyCodes.length; i++) {
                if (keyCode == this.allowedKeyCodes[i]) {
                    return true;
                }
            }
            return false;
        }
    }
}