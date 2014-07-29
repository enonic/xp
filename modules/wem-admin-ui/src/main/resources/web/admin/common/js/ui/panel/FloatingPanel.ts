module api.ui.panel {

    export interface FloatingPanelOptions {
        draggable?:boolean;
        resizeable?:boolean
        draggableOptions?:JQueryUI.DraggableOptions;
    }

    export class FloatingPanel extends Panel {
        private draggable: boolean;
        private resizable: boolean;
        private options: FloatingPanelOptions;

        constructor(options: FloatingPanelOptions) {
            super("floating-panel");
            this.options = options;
            if (options) {
                if (options.draggable) {
                    this.setJQueryDraggable();
                }
            }
        }

        private setJQueryDraggable() {
            this.draggable = true;
            if (this.draggable) {
                wemjq(this.getHTMLElement()).draggable(this.options.draggableOptions);
            }
        }
    }

}