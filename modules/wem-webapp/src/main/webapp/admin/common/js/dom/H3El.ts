module api.dom {

    export class H3El extends Element {

        constructor(generateId?:boolean, className?:string) {
            super(new ElementProperties().setTagName("h3").setGenerateId(generateId).setClassName(className));
        }

        public setText(value: string) {
            this.getEl().setInnerHtml(value);
        }

    }
}
