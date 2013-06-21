module api_dom {

    export class SpanEl extends Element {

        constructor(idPrefix?:string, className?:string) {
            super('span', idPrefix, className);
        }
    }
}