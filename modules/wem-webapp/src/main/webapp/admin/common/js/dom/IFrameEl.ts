module api.dom {

    export class IFrameEl extends Element {

        private loaded: boolean = false;

        constructor(idPrefix?: string, className?: string, el?: ElementHelper) {
            super("iframe", idPrefix, className, el);
            this.getHTMLElement().onload = (event) => {
                this.loaded = true;
            }
        }

        static fromHtmlElement(element: HTMLIFrameElement): IFrameEl {
            return new IFrameEl(null, null, new ElementHelper(element));
        }

        public setSrc(src: string) {
            this.getEl().setAttribute("src", src);
        }

        isLoaded() {
            return this.loaded;
        }

        postMessage(data: {}, targetOrigin: string = "*") {
            var thisIFrameElement: HTMLIFrameElement = <HTMLIFrameElement>this.getHTMLElement();
            thisIFrameElement.contentWindow.postMessage(data, targetOrigin)
        }
    }
}
