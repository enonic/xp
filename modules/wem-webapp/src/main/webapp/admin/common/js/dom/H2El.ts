module api.dom {

    export class H2El extends Element {

        constructor(idPrefix?:string, className?:string) {
            super("h2", idPrefix, className);
        }
    }
}
