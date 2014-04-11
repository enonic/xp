module api.dom {

    export class H6El extends Element {

        constructor(className?:string) {
            super(new ElementProperties().setTagName("h6").setClassName(className));
        }

        setText(value: string): H6El {
            this.getEl().setInnerHtml(value);
            return this;
        }
    }
}
