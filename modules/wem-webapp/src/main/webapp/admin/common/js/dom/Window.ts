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

        onResized(listener: (event: UIEvent) => void) {
            this.el.addEventListener("resize", listener);
        }

        unResized(listener: (event: UIEvent) => void) {
            this.el.removeEventListener("resize", listener);
        }

    }

}