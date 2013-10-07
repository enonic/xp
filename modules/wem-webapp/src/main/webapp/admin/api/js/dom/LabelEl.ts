module api_dom {

    export class LabelEl extends Element {

        constructor(value:string, forElement?:Element, idPrefix?:string, className?:string) {
            super("label", idPrefix, className);
            this.setValue(value);
            if (forElement) {
                this.getEl().setAttribute("for", forElement.getId());
            }
        }

        setValue(value:string) {
            this.getEl().setInnerHtml(value);
        }
    }
}