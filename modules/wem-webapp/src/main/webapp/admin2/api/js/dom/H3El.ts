module api_dom {

    export class H3El extends Element {

        constructor(idPrefix?:string, className?:string) {
            super("h3", idPrefix, className);
        }
    }
}
