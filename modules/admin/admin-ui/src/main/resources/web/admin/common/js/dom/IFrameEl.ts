module api.dom {

    import UriHelper = api.util.UriHelper

    export class IFrameEl extends api.dom.Element {

        private loaded: boolean = false;

        constructor(className?: string) {
            super(new NewElementBuilder().
                setTagName("iframe").
                setClassName(className));

            this.onLoaded((event: UIEvent) => this.loaded = true);
        }

        public setSrc(src: string): api.dom.IFrameEl {
            this.getEl().setAttribute("src", src);
            return this;
        }

        isSrcAlreadyShown(src: string): boolean {
            var thisIFrameElement: HTMLIFrameElement = <HTMLIFrameElement>this.getHTMLElement();

            try {
                if (!!src && !!thisIFrameElement.contentWindow && thisIFrameElement.contentWindow.location) {
                    var href = thisIFrameElement.contentWindow.location.href;
                    return this.trimAnchor(src) === this.trimAnchor(this.getRelativeHref(href));
                }
            } catch (reason) {}

            return false;
        }

        private getRelativeHref(href: string) {
            var location: Location = (<HTMLIFrameElement>this.getHTMLElement()).contentWindow.location;
            return UriHelper.relativePath(href.replace(location.protocol + "//" + location.host, ""));
        }

        private trimAnchor(trimMe: string): string {
            var index = trimMe.lastIndexOf("#");
            return index >= 0 ? UriHelper.relativePath(trimMe.substring(0, index)) : UriHelper.relativePath(trimMe);
        }

        isLoaded() {
            return this.loaded;
        }

        postMessage(data: any, targetOrigin: string = "*") {
            var thisIFrameElement: HTMLIFrameElement = <HTMLIFrameElement>this.getHTMLElement();
            thisIFrameElement.contentWindow.postMessage(data, targetOrigin)
        }

        onLoaded(listener: (event: UIEvent) => void) {
            this.getEl().addEventListener("load", listener);
        }

        unLoaded(listener: (event: UIEvent) => void) {
            this.getEl().removeEventListener("load", listener);
        }
    }
}
