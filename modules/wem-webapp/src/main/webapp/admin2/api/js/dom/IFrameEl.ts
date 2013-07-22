module api_dom {

    export class IFrameEl extends Element {

        constructor(idPrefix?:string, className?:string) {
            super("iframe", idPrefix, className);
        }

        public setSrc(src:string) {
            this.getEl().setAttribute("src", src);
        }
    }
}
