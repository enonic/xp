module api_ui {

    export class ButtonEl extends api_ui.Element {

        constructor(idPrefix?:string, className?:string) {
            super("button", idPrefix, className);
        }
    }
}
