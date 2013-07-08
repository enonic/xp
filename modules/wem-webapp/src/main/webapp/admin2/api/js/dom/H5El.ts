module api_dom {

    export class H5El extends Element {

        constructor(idPrefix?:string, className?:string) {
            super("h5", idPrefix, className);
        }
    }
}
