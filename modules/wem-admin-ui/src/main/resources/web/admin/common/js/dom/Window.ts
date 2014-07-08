module api.dom {

    export class Window {

        private el: any; // Window clashes with api.dom.Window

        private static instance: Window = new Window();

        static get(): Window {
            return Window.instance;
        }

        constructor() {
            this.el = window;
        }

        getHTMLElement(): HTMLElement {
            return this.el;
        }

        getScrollTop(): number {
            return wemjq(this.el).scrollTop();
        }

        onResized(listener: (event: UIEvent) => void, element?: api.dom.Element) {
            this.el.addEventListener("resize", listener);

            if (element) {
                element.onRemoved(() => this.unResized(listener));
            }
        }

        unResized(listener: (event: UIEvent) => void) {
            this.el.removeEventListener("resize", listener);
        }

        onScroll(listener: (event: UIEvent) => void, element?: api.dom.Element) {
            this.el.addEventListener('scroll', listener);

            if (element) {
                element.onRemoved(() => this.unScroll(listener));
            }
        }

        unScroll(listener: (event: UIEvent) => void) {
            this.el.removeEventListener('scroll', listener);
        }
    }

}