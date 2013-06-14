module api_ui {

    export class H1El extends api_ui.Element {

        constructor(idPrefix?:string, className?:string) {
            super("h1", idPrefix, className);
        }
    }
}
