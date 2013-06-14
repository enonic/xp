module api_ui {

    export class UlEl extends api_ui.Element {

        constructor(idPrefix?:string, className?:string) {
            super("ul", idPrefix, className);
        }
    }
}
