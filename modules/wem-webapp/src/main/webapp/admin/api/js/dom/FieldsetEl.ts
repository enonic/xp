module api_dom {

    export class FieldsetEl extends Element {

        constructor(idPrefix?:string, className?:string) {
            super('fieldset', idPrefix, className);
        }
    }
}