module api_ui {

    export interface FloatingWindowOptions {
        draggable?:bool;
        resizeable?:bool;
    }

    export class FloatingWindow extends api_dom.DivEl {
        private draggable:bool;
        private resizable:bool;

        constructor(options:FloatingWindowOptions) {
            super("FloatingWindow");
            this.addClass("floating-window");
            if (options.draggable) {
                this.setDraggable();
            }
        }

        private setDraggable() {
            this.draggable = true;
            if (this.draggable) {
                jQuery(this.getHTMLElement()).draggable();
            }
        }
    }
}