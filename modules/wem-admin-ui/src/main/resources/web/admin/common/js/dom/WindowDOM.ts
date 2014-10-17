module api.dom {

    export class WindowDOM {

        private el: any; // Window clashes with api.dom.Window

        private static instance: WindowDOM = new WindowDOM();

        static get(): WindowDOM {
            return WindowDOM.instance;
        }

        constructor() {
            this.el = window;
        }

        asWindow(): Window {
            return this.el;
        }

        getTopParent(): WindowDOM {

            var parent = this.getParent();
            if (!parent) {
                return null;
            }

            var i = 0;
            do {
                var next = parent.getParent();
                if (!next) {
                    return parent;
                }
                parent = next;
                i++;
            }
            while (i < 10);
            return  null;
        }

        getParent(): WindowDOM {
            var parent = this.el.parent;
            if (parent === this.el) {
                return null;
            }
            return parent.api.dom.WindowDOM.get();
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