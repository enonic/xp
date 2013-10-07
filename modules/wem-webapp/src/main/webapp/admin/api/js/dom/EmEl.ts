module api_dom {

    export class EmEl extends Element {

        constructor(idPrefix?:string, className?:string) {
            super("em", idPrefix, className);
        }
    }
}
