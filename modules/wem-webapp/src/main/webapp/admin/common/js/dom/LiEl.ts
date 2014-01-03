module api.dom {

    export class LiEl extends Element {

        constructor(idPrefix?:string, className?:string) {
            super("li", idPrefix, className);
        }
    }
}
