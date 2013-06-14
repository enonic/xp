module api_ui {

    export class DivEl extends api_ui.Element {

        constructor(idPrefix?:string, className?:string) {
            super("div", idPrefix, className);
        }
    }
}
