module api_ui {

    export class Tooltip extends api_dom.DivEl {

        private target:api_dom.Element;
        private timeout:number;
        private side:string;
        private offset:number[];

        private hideTimeout:number;

        /**
         * Widget to show floating tooltips
         * @param target Element to show tooltip at
         * @param text Text of the tooltip
         * @param timeout Time to keep tooltip visible after leaving target
         * @param side Side of the target where tooltip should be shown (top,left,right,bottom)
         * @param offset Fine tuning of the tooltip positioning (defaults to the center of the side)
         */
            constructor(target:api_dom.Element, text:string, timeout?:number, side?:string, offset?:number[]) {
            super("Tooltip", "tooltip");

            this.target = target;
            this.timeout = timeout !== undefined ? timeout : 1000;
            this.side = side || "bottom";
            this.offset = offset || [0, 0];

            var me = this;
            var el = this.getEl();
            el.addClass(this.side);
            el.setInnerHtml(text);

            var anchorEl = new api_dom.DivEl("Tooltip", "tooltip-anchor");
            el.appendChild(anchorEl.getHTMLElement());

            el.addEventListener("mouseover", function (event:Event) {
                me.stopTimeout();
            });
            el.addEventListener("mouseout", function (event:Event) {
                me.startTimeout();
            });

            var targetEl = target.getEl();
            targetEl.addEventListener("mouseover", function (event:Event) {
                me.stopTimeout();
                if (!me.isVisible()) {
                    me.show();
                }
            });
            targetEl.addEventListener("mouseout", function (event:Event) {
                me.startTimeout();
            });

            document.body.appendChild(this.getHTMLElement());
        }

        show() {
            super.show();
            this.positionByTarget();
        }

        showFor(ms:number) {
            this.show();
            this.startTimeout(ms);
        }

        setTimeout(timeout:number) {
            this.timeout = timeout;
        }

        getTimeout() {
            return this.timeout;
        }

        setSide(side:string) {
            this.side = side;
        }

        getSide() {
            return this.side;
        }

        private positionByTarget() {

            var targetEl = this.target.getHTMLElement();
            var targetOffset = this.target.getEl().getOffset();
            var el = this.getHTMLElement();

            var offsetLeft, offsetTop;
            switch (this.side) {
            case "top":
                offsetLeft = targetOffset.left + (targetEl.offsetWidth - el.offsetWidth) / 2 + this.offset[0];
                offsetTop = targetOffset.top - el.offsetHeight + this.offset[1];
                break;
            case "bottom":
                offsetLeft = targetOffset.left + (targetEl.offsetWidth - el.offsetWidth) / 2 + this.offset[0];
                offsetTop = targetOffset.top + targetEl.offsetHeight + this.offset[1];
                break;
            case "left":
                offsetLeft = targetOffset.left - el.offsetWidth + this.offset[0];
                offsetTop = targetOffset.top + (targetEl.offsetHeight - el.offsetHeight) / 2 + this.offset[1];
                break;
            case "right":
                offsetLeft = targetOffset.left + targetEl.offsetWidth + this.offset[0];
                offsetTop = targetOffset.top + (targetEl.offsetHeight - el.offsetHeight) / 2 + this.offset[1];
                break;
            }

            // check screen edges
            if (offsetLeft < 0) {
                offsetLeft = 0;
            } else if (offsetLeft + el.offsetWidth > document.body.clientWidth) {
                offsetLeft = document.body.clientWidth - el.offsetWidth;
            }
            if (offsetTop < 0) {
                offsetTop = 0;
            } else if (offsetTop + el.offsetHeight > document.body.clientHeight) {
                offsetTop = document.body.clientHeight - el.offsetHeight;
            }

            jQuery(this.getHTMLElement()).offset({
                left: offsetLeft,
                top: offsetTop
            });
        }

        private startTimeout(ms?:number) {

            this.stopTimeout();

            var me = this;
            if (this.timeout > 0) {
                this.hideTimeout = setTimeout(function () {
                    me.hide();
                }, ms || this.timeout);
            } else {
                me.hide();
            }
        }

        private stopTimeout() {
            if (this.hideTimeout) {
                clearTimeout(this.hideTimeout);
                this.hideTimeout = undefined;
            }
        }

    }


}