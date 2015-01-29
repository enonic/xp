module api.ui.text {

    export class TextInput extends api.dom.InputEl {

        static MIDDLE: string = 'middle';
        static LARGE: string = 'large';

        /**
         * Specifies RegExp for characters that will be removed during input.
         */
        private stripCharsRe: RegExp;
        /**
         * Forbidden chars filters out keyCodes for delete, backspace and arrow buttons in Firefox, so we need to
         * allow these to pass the filter (8=backspace, 9=tab, 46=delete, 39=right arrow, 47=left arrow)
         */
        private allowedKeyCodes: number[] = [8, 9, 46, 39, 37];

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

            this.onKeyPressed((event: KeyboardEvent) => {
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

            this.onInput((event: Event) => {
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

            super.setValue(value);

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
            super.setPlaceholder(value);
            return this;
        }

        setForbiddenCharsRe(re: RegExp): TextInput {
            this.stripCharsRe = re;
            return this;
        }

        selectText(from?: number, to?: number) {
            var htmlEl = <any>this.getHTMLElement();

            if (!from) {
                (<HTMLInputElement>htmlEl).select();
            } else if (!to) {
                to = this.getValue().length;
            }

            if (htmlEl.createTextRange) {
                var selRange = htmlEl.createTextRange();
                selRange.collapse(true);
                selRange.moveStart('character', from);
                selRange.moveEnd('character', to);
                selRange.select();
            } else if (htmlEl.setSelectionRange) {
                htmlEl.setSelectionRange(from, to);
            } else if (htmlEl.selectionStart) {
                htmlEl.selectionStart = from;
                htmlEl.selectionEnd = to;
            }
            htmlEl.focus();
        }

        onValueChanged(listener: (event: ValueChangedEvent)=>void) {
            this.valueChangedListeners.push(listener);
        }

        unValueChanged(listener: (event: ValueChangedEvent)=>void) {
            this.valueChangedListeners = this.valueChangedListeners.filter((currentListener: (event: ValueChangedEvent)=>void) => {
                return listener != currentListener;
            });
        }

        updateValidationStatusOnUserInput(isValid: boolean) {
            if (isValid) {
                this.removeClass("invalid");
                if (!api.util.StringHelper.isEmpty(this.getValue())) {
                    this.addClass("valid");
                } else {
                    this.removeClass("valid");
                }
            } else {
                this.removeClass("valid");
                this.addClass("invalid");
            }
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