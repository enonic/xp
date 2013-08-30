module api_ui {

    export interface FloatingWindowOptions {
        draggable?:boolean;
        resizeable?:boolean;
    }

    export class FloatingWindow extends api_dom.DivEl {
        private draggable:boolean;
        private resizable:boolean;

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