module api.dom {

    export class H1El extends Element {

        constructor(className?:string) {
            super(new ElementProperties().setTagName("h1").setClassName(className));
        }

        setText(value: string) {
            this.getEl().setInnerHtml(value)
        }
    }
}
