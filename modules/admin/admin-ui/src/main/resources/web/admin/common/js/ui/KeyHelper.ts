module api.ui {

    export class KeyHelper {

        static isNumber(event: KeyboardEvent): boolean {
            return event.keyCode >= 48 && event.keyCode <= 57;
        }

        static isDash(event: KeyboardEvent): boolean {
            return event.keyCode === 189;
        }

        static isDel(event: KeyboardEvent): boolean {
            return event.keyCode === 46;
        }

        static isBackspace(event: KeyboardEvent): boolean {
            return event.keyCode === 8;
        }

        static isColon(event: KeyboardEvent): boolean {
            return event.keyCode === 186;
        }

        static isComma(event: KeyboardEvent): boolean {
            return event.keyCode === 188;
        }

        static isDot(event: KeyboardEvent): boolean {
            return event.keyCode === 190;
        }

        static isArrowKey(event: KeyboardEvent): boolean {
            return event.keyCode >= 37 && event.keyCode <= 40;
        }

        static isArrowLeftKey(event: KeyboardEvent): boolean {
            return event.keyCode === 37;
        }

        static isArrowRightKey(event: KeyboardEvent): boolean {
            return event.keyCode === 39;
        }

        static isControlKey(event: KeyboardEvent): boolean {
            return event.keyCode === 17;
        }

        static isShiftKey(event: KeyboardEvent): boolean {
            return event.keyCode === 16;
        }

        static isAltKey(event: KeyboardEvent): boolean {
            return event.keyCode === 18;
        }

        static isMetaKey(event: KeyboardEvent): boolean {
            return event.keyCode === 224 || //FF
                   event.keyCode === 17 || // Opera
                   event.keyCode === 91 || event.keyCode === 93; // Safari/Chrome
        }

        static isTabKey(event: KeyboardEvent): boolean {
            return event.keyCode === 9;
        }

        static isModifierKey(event: KeyboardEvent): boolean {
            return KeyHelper.isControlKey(event) || KeyHelper.isShiftKey(event) || KeyHelper.isAltKey(event) || KeyHelper.isMetaKey(event);
        }

        static isEscKey(event: KeyboardEvent): boolean {
            return event.keyCode === 27;
        }

        static isEnterKey(event: KeyboardEvent): boolean {
            return event.keyCode === 13;
        }
    }
}