module api_dom {

    export class SelectEl extends Element {

        constructor(idPrefix?:string, className?:string) {
            super("select", idPrefix, className);
        }
    }
}
