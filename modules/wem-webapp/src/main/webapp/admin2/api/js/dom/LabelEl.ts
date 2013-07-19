module api_dom {

    export class LabelEl extends Element {

        constructor(idPrefix?:string, className?:string) {
            super("label", idPrefix, className);
        }
    }
}