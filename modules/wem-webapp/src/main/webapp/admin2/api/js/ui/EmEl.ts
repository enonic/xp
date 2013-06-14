module api_ui {

    export class EmEl extends api_ui.Element {

        constructor(idPrefix?:string, className?:string) {
            super("em", idPrefix, className);
        }
    }
}
