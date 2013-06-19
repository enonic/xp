module api_dom {

    export class ButtonEl extends Element {

        constructor(idPrefix?:string, className?:string) {
            super("button", idPrefix, className);
        }
    }
}
