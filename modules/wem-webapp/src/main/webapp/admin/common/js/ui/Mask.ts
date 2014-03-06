module api.ui {

    export class Mask extends api.dom.DivEl {

        private masked: api.dom.Element;

        constructor(itemToMask?: api.dom.Element) {
            super("mask");

            this.masked = itemToMask;
            if (this.masked) {
                this.masked.onHidden((event) => {
                    this.hide();
                });
                this.masked.onRemoved((event) => {
                    this.remove();
                });
                // Masked element might have been resized on window resize
                window.addEventListener("resize", () => {
                    this.positionOver(this.masked);
                });
            }
            api.dom.Body.get().appendChild(this);
        }

        show() {
            if (this.masked && this.masked.isVisible()) {
                this.positionOver(this.masked);
        }
            super.show();
        }

        private positionOver(masked: api.dom.Element) {
            var maskedEl = masked.getEl();
            var maskedOffsets = maskedEl.getOffset();
            var maskedWidth = maskedEl.getWidthWithBorder();
            var maskedHeight = maskedEl.getHeightWithBorder();

            var maskEl = this.getEl();
            maskEl.setTop(maskedOffsets.top + "px").setLeft(maskedOffsets.left + "px").setWidth(maskedWidth + "px").setHeight(maskedHeight + "px");
        }

        getMasked(): api.dom.Element {
            return this.masked;
        }

    }

}