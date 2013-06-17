module api_ui {

    export class SpanEl extends api_ui.Element {

        constructor(idPrefix?:string, className?:string) {
            super('span', idPrefix, className);
        }
    }
}