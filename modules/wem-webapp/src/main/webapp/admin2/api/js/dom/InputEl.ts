module api_dom {

    export class InputEl extends Element {

        constructor(idPrefix?:string, className?:string, type?:string) {
            super("input", idPrefix, className);
            this.getHTMLElement().setAttribute('type', type || 'text');
        }
    }
}
