module api_dom {

    export class H1El extends Element {

        constructor(idPrefix?:string, className?:string) {
            super("h1", idPrefix, className);
        }
    }
}
