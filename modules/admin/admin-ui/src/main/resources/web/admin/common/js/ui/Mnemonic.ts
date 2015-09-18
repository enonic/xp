module api.ui {

    export class Mnemonic {

        private value: string;

        constructor(value: string) {

            this.value = value;
        }

        getValue(): string {
            return this.value;
        }

        toKeyBinding(callback?: (e: ExtendedKeyboardEvent, combo: string) => any): KeyBinding {
            return new KeyBinding("alt+" + this.getValue(), callback);
        }

        underlineMnemonic(text: string): string {

            var mStart: number = text.indexOf(this.value);
            if (mStart == -1) {
                mStart = text.indexOf(this.value.toLowerCase());
                if (mStart == -1) {
                    mStart = text.indexOf(this.value.toUpperCase());
                }
            }
            var result = "";
            if (mStart > 0) {
                result = text.substr(0, mStart);
            }
            result += "<u>" + text.charAt(mStart) + "</u>";
            if (mStart < text.length - 1) {
                result += text.substr(mStart + 1, text.length);
            }

            return result;
        }
    }
}
