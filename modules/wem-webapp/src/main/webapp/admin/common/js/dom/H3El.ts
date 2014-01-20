module api.dom {

    export class H3El extends Element {

        constructor(className?:string) {
            super(new ElementProperties().setTagName("h3").setClassName(className));
        }

        public setText(value: string) {
            this.getEl().setInnerHtml(value);
        }

    }
}
