module api_dom {

    export class LegendEl extends Element {

        constructor(legend:string, idPrefix?:string, className?:string) {
            super('legend', idPrefix, className);
            this.getEl().setInnerHtml(legend);
        }
    }
}