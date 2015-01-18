module api.ui {

    export class KeyHelper {

        static isNumber(event: KeyboardEvent): boolean {
            return event.keyCode >= 48 && event.keyCode <= 57;
        }

        static isDash(event: KeyboardEvent): boolean {
            return event.keyCode == 189;
        }

        static isDel(event: KeyboardEvent): boolean {
            return event.keyCode == 46;
        }

        static isBackspace(event: KeyboardEvent): boolean {
            return event.keyCode == 8;
        }
    }
}