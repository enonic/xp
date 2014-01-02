module api.ui {

    export interface FloatingWindowOptions {
        draggable?:boolean;
        resizeable?:boolean
        draggableOptions?:JQueryUI.DraggableOptions;
    }

    export class FloatingWindow extends api.dom.DivEl {
        private draggable:boolean;
        private resizable:boolean;
        private options:FloatingWindowOptions;

        constructor(options:FloatingWindowOptions) {
            super("FloatingWindow");
            this.addClass("floating-window");
            this.options = options;
            if (options) {
                if (options.draggable) {
                    this.setDraggable();
                }
            }
        }

        private setDraggable() {
            this.draggable = true;
            if (this.draggable) {
                jQuery(this.getHTMLElement()).draggable(this.options.draggableOptions);
            }
        }
    }

}