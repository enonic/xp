module api.dom {

    import UriHelper = api.util.UriHelper;

    export class IFrameEl extends api.dom.Element {

        private loaded: boolean = false;

        constructor(className?: string) {
            super(new NewElementBuilder().
                setTagName('iframe').
                setClassName(className));

            this.onLoaded((event: UIEvent) => this.loaded = true);
        }

        public setSrc(src: string): api.dom.IFrameEl {
            this.getEl().setAttribute('src', src);
            return this;
        }

        private getFrameWindowObject(): Window {
            return (<HTMLIFrameElement>this.getHTMLElement()).contentWindow;
        }

        isLoaded() {
            return this.loaded;
        }

        postMessage(data: any, targetOrigin: string = '*') {
            let thisIFrameElement: HTMLIFrameElement = <HTMLIFrameElement>this.getHTMLElement();
            thisIFrameElement.contentWindow.postMessage(data, targetOrigin);
        }

        onLoaded(listener: (event: UIEvent) => void) {
            this.getEl().addEventListener('load', listener);
        }

        unLoaded(listener: (event: UIEvent) => void) {
            this.getEl().removeEventListener('load', listener);
        }
    }
}
