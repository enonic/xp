module api_dom {

    export class H4El extends Element {

        constructor(idPrefix?:string, className?:string) {
            super("h4", idPrefix, className);
        }
    }
}
