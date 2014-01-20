module api.ui {
    export class DraggingMask extends api.dom.DivEl {
        private maskedItem:api.dom.Element;

        constructor(itemToMask:api.dom.Element) {
            super("dragging-mask");
            this.maskedItem = itemToMask;
            this.getEl().setDisplay("none");
        }

        show() {
            var maskedHeight = this.maskedItem.getHTMLElement().offsetHeight;
            var maskedWidth = this.maskedItem.getHTMLElement().offsetWidth;
            var maskedTop = this.maskedItem.getHTMLElement().offsetTop;
            var maskedLeft = this.maskedItem.getHTMLElement().offsetLeft;
            this.getEl().setHeight(maskedHeight + "px");
            this.getEl().setWidth(maskedWidth + "px");
            this.getEl().setTop(maskedTop + "px");
            this.getEl().setLeft(maskedLeft + "px");

            jQuery(this.getHTMLElement()).fadeIn(200);
        }

        hide() {
            super.hide();
        }
    }
}