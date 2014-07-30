module api.ui.mask {

    export class DragMask extends Mask {

        constructor(itemToMask: api.dom.Element) {
            super(itemToMask);
            this.addClass("drag-mask");
        }
    }
}