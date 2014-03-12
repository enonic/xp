module api.dom {

    export class ButtonEl extends Element {

        constructor(className?:string) {
            super(new ElementProperties().setTagName("button").setClassName(className));
        }

        setText(text:string) {
            this.getEl().setInnerHtml(text);
        }
    }
}
