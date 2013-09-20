module app_contextwindow {
    export class DraggingMask extends api_dom.DivEl {
        private maskedItem:api_dom.Element;

        constructor(itemToMask:api_dom.Element) {
            super("DraggingMask");
            this.addClass("dragging-mask");
            this.maskedItem = itemToMask;
            this.getEl().setDisplay("none");
        }

        show() {
            var maskedHeight = this.maskedItem.getHTMLElement().offsetHeight;
            var maskedWidth = this.maskedItem.getHTMLElement().offsetWidth;
            this.getEl().setHeight(maskedHeight + "px");
            this.getEl().setWidth(maskedWidth + "px");

            jQuery(this.getHTMLElement()).fadeIn(200);
        }

        hide() {
            super.hide();
        }
    }
}