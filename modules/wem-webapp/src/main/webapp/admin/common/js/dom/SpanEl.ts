module api.dom {

    export class SpanEl extends Element {

        constructor(generateId?:boolean, className?:string) {
            super(new ElementProperties().setTagName("span").setGenerateId(generateId).setClassName(className));
        }

        setHtml(html:string) {
            this.getEl().setInnerHtml(html);
        }
    }
}