module api.dom {

    export class IFrameEl extends Element {

        private loaded: boolean = false;

        constructor(className?: string, elHelper?: ElementHelper) {
            super(new ElementProperties().setTagName("iframe").setClassName(className).setHelper(elHelper));
            this.getHTMLElement().onload = (event) => {
                this.loaded = true;
            }
        }

        static fromHtmlElement(element: HTMLIFrameElement): IFrameEl {
            return new IFrameEl(null, new ElementHelper(element));
        }

        public setSrc(src: string) {
            this.getEl().setAttribute("src", src);
        }

        isLoaded() {
            return this.loaded;
        }

        postMessage(data: any, targetOrigin: string = "*") {
            var thisIFrameElement: HTMLIFrameElement = <HTMLIFrameElement>this.getHTMLElement();
            thisIFrameElement.contentWindow.postMessage(data, targetOrigin)
        }
    }
}
