module api_dom {

    export class InputEl extends Element {

        constructor(idPrefix?:string, className?:string) {
            super("input", idPrefix, className);
        }
    }
}
