module api_dom {

    export class LabelEl extends Element {

        constructor(name?:string, forElement?:Element, idPrefix?:string, className?:string) {
            super("label", idPrefix, className);
            if (name) {
                this.getEl().setInnerHtml(name);
            }
            if (forElement) {
                this.getEl().setAttribute("for", forElement.getId());
            }
        }
    }
}