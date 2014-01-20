module api.dom {

    export class SpanEl extends Element {

        constructor(className?:string) {
            super(new ElementProperties().setTagName("span").setClassName(className));
        }

        setHtml(html:string) {
            this.getEl().setInnerHtml(html);
        }
    }
}