module api_dom {

    export class FormEl extends Element {

        constructor(idPrefix?:string, className?:string) {
            super("form", idPrefix, className);
        }
    }
}
