module api.dom {

    export class LegendEl extends Element {

        constructor(legend:string, generateId?:boolean, className?:string) {
            super(new ElementProperties().setTagName("legend").setGenerateId(generateId).setClassName(className));
            this.getEl().setInnerHtml(legend);
        }
    }
}