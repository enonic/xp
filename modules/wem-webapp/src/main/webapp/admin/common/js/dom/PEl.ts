module api_dom {

    export class PEl extends Element {

        constructor(idPrefix?:string, className?:string) {
            super("p", idPrefix, className);
        }
    }
}
