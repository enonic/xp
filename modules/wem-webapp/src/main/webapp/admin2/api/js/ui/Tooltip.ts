module api_ui {

    export class Tooltip {

        static SIDE_TOP = "top";
        static SIDE_RIGHT = "right";
        static SIDE_BOTTOM = "bottom";
        static SIDE_LEFT = "left";

        static TRIGGER_MOUSE = "mouse";
        static TRIGGER_FOCUS = "focus";

        private tooltipEl:api_dom.DivEl;
        private static timeoutTimer:number;

        private target:api_dom.Element;
        private text:string;
        private timeout:number;
        private trigger:string;
        private side:string;
        private offset:number[];

        /*
         * Widget to show floating tooltips
         * @param target Element to show tooltip at
         * @param text Text of the tooltip
         * @param timeout Time to keep tooltip visible after leaving target
         * @param trigger Event type to hook on (mouse,focus)
         * @param side Side of the target where tooltip should be shown (top,left,right,bottom)
         * @param offset Fine tuning of the tooltip positioning (defaults to the center of the side)
         */
        constructor(target:api_dom.Element, text:string, timeout?:number = 1000, trigger?:string = Tooltip.TRIGGER_MOUSE,
                    side?:string = Tooltip.SIDE_BOTTOM, offset?:number[] = [0, 0]) {

            this.target = target;
            this.text = text;
            this.timeout = timeout;
            this.trigger = trigger;
            this.side = side;
            this.offset = offset;


            var targetEl = target.getEl();
            targetEl.addEventListener(this.getEventName(true), (event:Event) => {
                this.stopTimeout();
                this.show();
            });
            targetEl.addEventListener(this.getEventName(false), (event:Event) => {
                this.startTimeout();
            });
        }

        show() {
            if (!this.tooltipEl) {
                this.tooltipEl = new api_dom.DivEl("Tooltip", "tooltip " + this.side);
                this.tooltipEl.getEl().setInnerHtml(this.text).setClass("tooltip " + this.side);
                this.target.getParent().appendChild(this.tooltipEl);
                this.tooltipEl.show();
                this.positionByTarget();
            }

        }

        hide() {
            if (this.tooltipEl) {
                this.tooltipEl.getEl().remove();
                this.tooltipEl = null;
            }
        }

        showFor(ms:number) {
            this.show();
            this.startTimeout(ms);
        }

        setTimeout(timeout:number) {
            this.timeout = timeout;
            return this;
        }

        getTimeout() {
            return this.timeout;
        }

        setTrigger(trigger:string) {
            this.trigger = trigger;
        }

        getTrigger() {
            return this.trigger;
        }

        setSide(side:string) {
            this.side = side;
            return this;
        }

        getSide() {
            return this.side;
        }

        setOffset(offset:number[]) {
            this.offset = offset;
            return this;
        }

        getOffset() {
            return this.offset;
        }

        private positionByTarget() {

            var targetEl = this.target.getHTMLElement();
            var targetOffset = this.target.getEl().getOffset();
            var el = this.tooltipEl.getHTMLElement();

            var offsetLeft, offsetTop;
            switch (this.side) {
            case Tooltip.SIDE_TOP:
                offsetLeft = targetOffset.left + (targetEl.offsetWidth - el.offsetWidth) / 2 + this.offset[0];
                offsetTop = targetOffset.top - el.offsetHeight + this.offset[1];
                break;
            case Tooltip.SIDE_BOTTOM:
                offsetLeft = targetOffset.left + (targetEl.offsetWidth - el.offsetWidth) / 2 + this.offset[0];
                offsetTop = targetOffset.top + targetEl.offsetHeight + this.offset[1];
                break;
            case Tooltip.SIDE_LEFT:
                offsetLeft = targetOffset.left - el.offsetWidth + this.offset[0];
                offsetTop = targetOffset.top + (targetEl.offsetHeight - el.offsetHeight) / 2 + this.offset[1];
                break;
            case Tooltip.SIDE_RIGHT:
                offsetLeft = targetOffset.left + targetEl.offsetWidth + this.offset[0];
                offsetTop = targetOffset.top + (targetEl.offsetHeight - el.offsetHeight) / 2 + this.offset[1];
                break;
            }

            var bodyEl = api_dom.Body.get().getHTMLElement();
            // check screen edges
            if (offsetLeft < 0) {
                offsetLeft = 0;
            } else if (offsetLeft + el.offsetWidth > bodyEl.clientWidth) {
                offsetLeft = bodyEl.clientWidth - el.offsetWidth;
            }
            if (offsetTop < 0) {
                offsetTop = 0;
            } else if (offsetTop + el.offsetHeight > bodyEl.clientHeight) {
                offsetTop = bodyEl.clientHeight - el.offsetHeight;
            }

            jQuery(el).offset({
                left: offsetLeft,
                top: offsetTop
            });
        }

        private startTimeout(ms?:number) {

            this.stopTimeout();
            var t = ms || this.timeout;
            if (t > 0) {
                Tooltip.timeoutTimer = setTimeout(() => {
                    this.hide();
                }, t);
            } else {
                this.hide();
            }
        }

        private stopTimeout() {
            if (Tooltip.timeoutTimer) {
                clearTimeout(Tooltip.timeoutTimer);
                Tooltip.timeoutTimer = undefined;
            }
        }

        private getEventName(enter:bool) {
            switch (this.trigger) {
            case Tooltip.TRIGGER_FOCUS:
                return enter ? "focus" : "blur";
            case Tooltip.TRIGGER_MOUSE:
            default:
                return enter ? "mouseover" : "mouseout";
                break;
            }
        }

    }

}