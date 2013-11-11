module api_dom {

    export class IFrameEl extends Element {

        private loaded:boolean = false;

        constructor(idPrefix?:string, className?:string, el?:ElementHelper) {
            super("iframe", idPrefix, className, el);
            this.getHTMLElement().onload = (event) => {
                this.setLoaded(true);
            }
        }

        static fromHtmlElement(element:HTMLIFrameElement):IFrameEl  {
            return new IFrameEl(null, null, new ElementHelper(element));
        }

        public setSrc(src:string) {
            this.getEl().setAttribute("src", src);
        }

        setLoaded(value:boolean) {
            this.loaded = value;
        }

        isLoaded() {
            return this.loaded;
        }
    }
}
