module api_dom {

    export class IFrameEl extends Element {

        constructor(idPrefix?:string, className?:string, el?:ElementHelper) {
            super("iframe", idPrefix, className, el);
        }

        static fromHtmlElement(element:HTMLIFrameElement):IFrameEl  {
            return new IFrameEl(null, null, new ElementHelper(element));
        }

        public setSrc(src:string) {
            this.getEl().setAttribute("src", src);
        }
    }
}
