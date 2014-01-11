module api.dom {

    export class IFrameEl extends Element {

        private loaded: boolean = false;

        constructor(generateId?: boolean, className?: string, elHelper?: ElementHelper) {
            super(new ElementProperties().setTagName("iframe").setGenerateId(generateId).setClassName(className).setHelper(elHelper));
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
