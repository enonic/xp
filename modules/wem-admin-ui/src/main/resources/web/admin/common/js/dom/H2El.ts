module api.dom {

    export class H2El extends Element {

        constructor(className?:string) {
            super(new ElementProperties().setTagName("h2").setClassName(className));
        }

        setText(value: string) {
            this.getEl().setInnerHtml(value)
        }
    }
}
