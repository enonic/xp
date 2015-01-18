module api.ui.mask {

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
                api.dom.WindowDOM.get().onResized((event: UIEvent) => {
                    if (this.isVisible()) {
                        this.positionOver(this.masked);
                    }
                }, this);
            }
            api.dom.Body.get().appendChild(this);
        }

        show() {
            super.show();
            if (this.masked) {
                this.positionOver(this.masked);
            }
        }

        private positionOver(masked: api.dom.Element) {
            var maskedEl = masked.getEl(),
                maskEl = this.getEl(),
                maskedOffset: {top:number; left: number},
                isMaskedPositioned = maskedEl.getPosition() != 'static',
                maskedDimensions: {width: string; height: string} = {
                    width: maskedEl.getWidthWithBorder() + "px",
                    height: maskedEl.getHeightWithBorder() + "px"
                };

            if (masked.contains(this) && isMaskedPositioned) {
                // mask is inside masked element & it is positioned
                maskedOffset = {
                    top: 0,
                    left: 0
                };

                if (maskedEl.getPosition() == 'absolute') {
                    maskedDimensions = {
                        width: '100%',
                        height: '100%'
                    };
                }
            } else {
                // mask is outside masked element
                var maskedParent = maskedEl.getOffsetParent(),
                    maskParent = maskEl.getOffsetParent();

                maskedOffset = maskedEl.getOffsetToParent();

                if (maskedParent != maskParent) {
                    // they have different offset parents so calc the difference
                    var maskedParentOffset = new api.dom.ElementHelper(maskedParent).getOffset(),
                        maskParentOffset = new api.dom.ElementHelper(maskParent).getOffset();

                    maskedOffset.left = maskedOffset.left + (maskedParentOffset.left - maskParentOffset.left);
                    maskedOffset.top = maskedOffset.top + (maskedParentOffset.top - maskParentOffset.top);
                }

                if (!isMaskedPositioned) {
                    // account for margins if masked is positioned statically
                    maskedOffset.top += maskedEl.getMarginTop();
                    maskedOffset.left += maskedEl.getMarginLeft();
                }
            }

            this.getEl().
                setTopPx(maskedOffset.top).
                setLeftPx(maskedOffset.left).
                setWidth(maskedDimensions.width).
                setHeight(maskedDimensions.height);
        }

        getMasked(): api.dom.Element {
            return this.masked;
        }

    }

}