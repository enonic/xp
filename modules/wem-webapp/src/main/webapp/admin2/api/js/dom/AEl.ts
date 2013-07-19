module api_dom {

    export class AEl extends Element {

        constructor(idPrefix?:string, className?:string) {
            super("a", idPrefix, className);
        }
    }
}
