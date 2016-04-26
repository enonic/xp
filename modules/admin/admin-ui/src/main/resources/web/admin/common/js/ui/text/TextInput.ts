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

        private previousValue: string;

        constructor(className?: string, size?: string, originalValue?: string) {
            super("text-input", 'text', api.StyleHelper.COMMON_PREFIX, originalValue);
            if (className) {
                this.addClass(className);
            }
            
            if (size) {
                this.addClassEx(size);
            }

            this.previousValue = this.getValue();

            this.onKeyUp((event: KeyboardEvent) => {
                if (event.keyCode == 27) {
                    this.setPreviousValue();
                    this.getEl().blur();
                }
            });

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

            this.onFocus((event: Event) => {
                this.previousValue = this.doGetValue();
            });

            this.onBlur((event: Event) => {
                this.previousValue = this.doGetValue();
            });
        }

        private setPreviousValue() {
            this.setValue(this.previousValue);
        }

        static large(className?: string, originalValue?: string): TextInput {
            return new TextInput(className, TextInput.LARGE, originalValue);
        }

        static middle(className?: string, originalValue?: string): TextInput {
            return new TextInput(className, TextInput.MIDDLE, originalValue);
        }

        protected doSetValue(value: string, silent?: boolean) {
            var newValue = this.removeForbiddenChars(value);
            super.doSetValue(newValue);
        }

        setForbiddenCharsRe(re: RegExp): TextInput {
            this.stripCharsRe = re;
            return this;
        }

        selectText(from?: number, to?: number) {
            var htmlEl = <HTMLInputElement> this.getHTMLElement();

            if (!from) {
                htmlEl.select();
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

        disableAutocomplete(): TextInput {
            this.getEl().setAttribute('autocomplete', 'off');
            return this;
        }

        moveCaretTo(pos) {
            this.selectText(pos, pos);
        }

        updateValidationStatusOnUserInput(isValid: boolean) {
            if (isValid) {
                this.removeClass("invalid");
                this.toggleClass("valid", !api.util.StringHelper.isEmpty(this.getValue()));
            } else {
                this.removeClass("valid");
                this.addClass("invalid");
            }
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
