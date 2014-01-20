module api.dom {

    export class LabelEl extends Element {

        constructor(value:string, forElement?:Element, className?:string) {
            super(new ElementProperties().setTagName("label").setClassName(className));
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