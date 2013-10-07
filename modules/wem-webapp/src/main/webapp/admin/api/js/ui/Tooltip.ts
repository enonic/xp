module api_ui {

    export class Tooltip {

        static SIDE_TOP = "top";
        static SIDE_RIGHT = "right";
        static SIDE_BOTTOM = "bottom";
        static SIDE_LEFT = "left";

        static TRIGGER_MOUSE = "mouse";
        static TRIGGER_FOCUS = "focus";

        private tooltipEl:api_dom.DivEl;
        private timeoutTimer:number;

        private target:api_dom.Element;
        private text:string;
        private showDelay:number;
        private hideTimeout:number;
        private trigger:string;
        private side:string;

        /*
         * Widget to show floating tooltips
         * Tooltip position can be adjusted in css using left,top attributes
         *
         * @param target Element to show tooltip at
         * @param text Text of the tooltip
         * @param hideTimeout Time to keep tooltip visible after leaving target
         * @param showDelay Time to hover mouse on target before showing tooltip
         * @param trigger Event type to hook on (mouse,focus)
         * @param side Side of the target where tooltip should be shown (top,left,right,bottom)
         */
        constructor(target:api_dom.Element, text:string, showDelay:number = 0, hideTimeout:number = 1000,
                    trigger:string = Tooltip.TRIGGER_MOUSE, side:string = Tooltip.SIDE_BOTTOM) {

            this.target = target;
            this.text = text;
            this.showDelay = showDelay;
            this.hideTimeout = hideTimeout;
            this.trigger = trigger;
            this.side = side;

            var targetEl = target.getEl();
            targetEl.addEventListener(this.getEventName(true), (event:Event) => {
                this.stopTimeout();
                this.startShowDelay();
            });
            targetEl.addEventListener(this.getEventName(false), (event:Event) => {
                this.startHideTimeout();
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
                this.tooltipEl.remove();
                this.tooltipEl = null;
            }
        }

        showAfter(ms:number):Tooltip {
            this.startShowDelay(ms);
            return this;
        }

        showFor(ms:number):Tooltip {
            this.show();
            this.startHideTimeout(ms);
            return this;
        }

        setHideTimeout(timeout:number):Tooltip {
            this.hideTimeout = timeout;
            return this;
        }

        getHideTimeout():number {
            return this.hideTimeout;
        }

        setShowDelay(delay:number):Tooltip {
            this.showDelay = delay;
            return this;
        }

        getShowDelay():number {
            return this.showDelay;
        }

        setTrigger(trigger:string):Tooltip {
            this.trigger = trigger;
            return this;
        }

        getTrigger():string {
            return this.trigger;
        }

        setSide(side:string):Tooltip {
            this.side = side;
            return this;
        }

        getSide():string {
            return this.side;
        }

        private positionByTarget() {

            var targetEl = this.target.getHTMLElement();
            var targetOffset = this.target.getEl().getOffset();
            var el = this.tooltipEl.getHTMLElement();
            var $el = jQuery(el);
            var elOffset = {
                left: parseInt($el.css('left')) || 0,
                top: parseInt($el.css('top')) || 0
            };

            var offsetLeft, offsetTop;
            switch (this.side) {
            case Tooltip.SIDE_TOP:
                offsetLeft = targetOffset.left + (targetEl.offsetWidth - el.offsetWidth) / 2 + elOffset.left;
                offsetTop = targetOffset.top - el.offsetHeight + elOffset.top;
                break;
            case Tooltip.SIDE_BOTTOM:
                offsetLeft = targetOffset.left + (targetEl.offsetWidth - el.offsetWidth) / 2 + elOffset.left;
                offsetTop = targetOffset.top + targetEl.offsetHeight + elOffset.top;
                break;
            case Tooltip.SIDE_LEFT:
                offsetLeft = targetOffset.left - el.offsetWidth + elOffset.left;
                offsetTop = targetOffset.top + (targetEl.offsetHeight - el.offsetHeight) / 2 + elOffset.top;
                break;
            case Tooltip.SIDE_RIGHT:
                offsetLeft = targetOffset.left + targetEl.offsetWidth + elOffset.left;
                offsetTop = targetOffset.top + (targetEl.offsetHeight - el.offsetHeight) / 2 + elOffset.top;
                break;
            }

            // check screen edges
            if (offsetLeft < 0) {
                offsetLeft = 0;
            } else if (offsetLeft + el.offsetWidth > window.innerWidth) {
                offsetLeft = window.innerWidth - el.offsetWidth;
            }
            if (offsetTop < 0) {
                offsetTop = 0;
            } else if (offsetTop + el.offsetHeight > window.innerHeight) {
                offsetTop = window.innerHeight - el.offsetHeight;
            }

            $el.offset({
                left: offsetLeft,
                top: offsetTop
            });
        }

        private startHideTimeout(ms?:number) {

            this.stopTimeout();
            var t = ms || this.hideTimeout;
            if (t > 0) {
                this.timeoutTimer = setTimeout(() => {
                    this.hide();
                }, t);
            } else {
                this.hide();
            }
        }

        private startShowDelay(ms?:number) {

            this.stopTimeout();
            var t = ms || this.showDelay;
            if(t > 0) {
                this.timeoutTimer = setTimeout(() => {
                    this.show();
                }, t);
            } else {
                this.show();
            }
        }

        private stopTimeout() {
            if (this.timeoutTimer) {
                clearTimeout(this.timeoutTimer);
                this.timeoutTimer = undefined;
            }
        }

        private getEventName(enter:boolean) {
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