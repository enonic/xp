module api_dom {

    export class H6El extends Element {

        constructor(idPrefix?:string, className?:string) {
            super("h6", idPrefix, className);
        }
    }
}
