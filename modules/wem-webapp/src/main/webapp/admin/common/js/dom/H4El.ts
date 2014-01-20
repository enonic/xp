module api.dom {

    export class H4El extends Element {

        constructor(className?:string) {
            super(new ElementProperties().setTagName("h4").setClassName(className));
        }

        setText(value: string) {
            this.getEl().setInnerHtml(value)
        }
    }
}
