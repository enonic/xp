module api.ui {

    export class Tooltip {

        static SIDE_TOP = "top";
        static SIDE_RIGHT = "right";
        static SIDE_BOTTOM = "bottom";
        static SIDE_LEFT = "left";

        static TRIGGER_HOVER = "hover";
        static TRIGGER_FOCUS = "focus";
        static TRIGGER_NONE = "none";

        static MODE_STATIC = "static";
        static MODE_FOLLOW = "follow";

        private static multipleAllowed: boolean = true;
        private static instances: Tooltip[] = [];

        private tooltipEl: api.dom.DivEl;
        private timeoutTimer: number;

        private overListener: (event: MouseEvent) => any;
        private outListener: (event: MouseEvent) => any;
        private moveListener: (event: MouseEvent) => any;

        private targetEl: api.dom.Element;
        private text: string;
        private contentEl: api.dom.Element;
        private showDelay: number;
        private hideTimeout: number;
        private trigger: string;
        private side: string;
        private mode: string;

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
        constructor(target: api.dom.Element, text?: string, showDelay: number = 0, hideTimeout: number = 0) {

            this.targetEl = target;
            this.text = text;
            this.showDelay = showDelay;
            this.hideTimeout = hideTimeout;
            this.side = Tooltip.SIDE_BOTTOM;

            this.overListener = (event: MouseEvent) => {
                this.startShowDelay();
            };
            this.outListener = (event: MouseEvent) => {
                this.startHideTimeout();
            };
            this.moveListener = (event: MouseEvent) => {
                if (this.tooltipEl && this.tooltipEl.isVisible()) {
                    this.positionAtMouse(event);
                }
            };

            this.setTrigger(Tooltip.TRIGGER_HOVER);
            this.setMode(Tooltip.MODE_STATIC);

            Tooltip.instances.push(this);
        }

        show() {
            this.stopTimeout();
            if (!this.tooltipEl) {
                this.tooltipEl = new api.dom.DivEl("tooltip " + this.side);
                if (this.contentEl) {
                    this.tooltipEl.appendChild(this.contentEl);
                } else {
                    this.tooltipEl.getEl().setInnerHtml(this.text);
                }

                var appendTo;
                if (this.mode == Tooltip.MODE_STATIC) {
                    appendTo = this.targetEl.getParentElement() || this.targetEl;
                } else {
                    appendTo = api.dom.Body.get();
                }
                appendTo.appendChild(this.tooltipEl);

                if (!Tooltip.multipleAllowed) {
                    this.hideOtherInstances();
                }
                this.tooltipEl.show();

                if (this.mode == Tooltip.MODE_STATIC) {
                    this.positionByTarget();
                }
            }
        }

        hide() {
            this.stopTimeout();
            if (this.tooltipEl) {
                this.tooltipEl.remove();
                this.tooltipEl = null;
            }
        }

        isVisible(): boolean {
            return this.tooltipEl && this.tooltipEl.isVisible();
        }

        showAfter(ms: number): Tooltip {
            this.startShowDelay(ms);
            return this;
        }

        showFor(ms: number): Tooltip {
            this.show();
            this.startHideTimeout(ms);
            return this;
        }

        setText(text: string): Tooltip {
            this.text = text;
            this.contentEl = undefined;
            return this;
        }

        getText(): string {
            return this.text;
        }

        setContent(content: api.dom.Element): Tooltip {
            this.contentEl = content;
            this.text = undefined;
            return this;
        }

        getContent(): api.dom.Element {
            return this.contentEl;
        }

        setHideTimeout(timeout: number): Tooltip {
            this.hideTimeout = timeout;
            return this;
        }

        getHideTimeout(): number {
            return this.hideTimeout;
        }

        setShowDelay(delay: number): Tooltip {
            this.showDelay = delay;
            return this;
        }

        getShowDelay(): number {
            return this.showDelay;
        }

        setTrigger(trigger: string): Tooltip {
            if (trigger == this.trigger) {
                return this;
            }

            // remove old listeners
            this.targetEl.getEl().
                removeEventListener(this.getEventName(true), this.overListener).
                removeEventListener(this.getEventName(false), this.outListener);

            this.trigger = trigger;

            // add new listeners
            if (trigger != Tooltip.TRIGGER_NONE) {
                this.targetEl.getEl().
                    addEventListener(this.getEventName(true), this.overListener).
                    addEventListener(this.getEventName(false), this.outListener);
            }
            return this;
        }

        getTrigger(): string {
            return this.trigger;
        }

        setSide(side: string): Tooltip {
            this.side = side;
            return this;
        }

        getSide(): string {
            return this.side;
        }

        setMode(mode: string): Tooltip {
            if (mode == this.mode) {
                return this;
            } else if (mode == Tooltip.MODE_STATIC) {
                api.dom.Body.get().unMouseMove(this.moveListener);
            } else if (mode == Tooltip.MODE_FOLLOW) {
                api.dom.Body.get().onMouseMove(this.moveListener);
            }
            this.mode = mode;
            return this;
        }

        getMode(): string {
            return this.mode;
        }

        private positionAtMouse(event: MouseEvent) {
            var left, top,
                x = event.clientX,
                y = event.clientY,
                el = this.tooltipEl.getEl(),
                windowEl = <any> api.dom.WindowDOM.get().getHTMLElement(),
                elProps = {
                    height: el.getHeightWithMargin(),
                    width: el.getWidthWithMargin(),
                    // if mode == follow, tooltip is appended to body, so window scroll can affect tooltip
                    scrollLeft: this.mode == Tooltip.MODE_FOLLOW ? windowEl.scrollX : 0,
                    scrollTop: this.mode == Tooltip.MODE_FOLLOW ? windowEl.scrollY : 0
                };
            switch (this.side) {
            case Tooltip.SIDE_TOP:
                left = x - elProps.width / 2 + elProps.scrollLeft;
                top = y - elProps.height + elProps.scrollTop;
                break;
            case Tooltip.SIDE_BOTTOM:
                left = x - elProps.width / 2 + elProps.scrollLeft;
                top = y + elProps.scrollTop;
                break;
            case Tooltip.SIDE_LEFT:
                left = x - elProps.width + elProps.scrollLeft;
                top = y - elProps.height / 2 + elProps.scrollTop;
                break;
            case Tooltip.SIDE_RIGHT:
                left = x + elProps.scrollLeft;
                top = y - elProps.height / 2 + elProps.scrollTop;
                break;
            }
            this.tooltipEl.getEl().setLeftPx(left);
            this.tooltipEl.getEl().setTopPx(top);
        }

        private positionByTarget() {

            var targetEl = this.targetEl.getHTMLElement(),
                targetOffset = this.targetEl.getEl().getOffset(),
                el = this.tooltipEl.getEl(),
                elProps = {
                    left: el.getMarginLeft() || 0,
                    top: el.getMarginTop() || 0,
                    height: el.getHeight(),
                    width: el.getWidth()
                };

            var offsetLeft, offsetTop;
            switch (this.side) {
            case Tooltip.SIDE_TOP:
                offsetLeft = targetOffset.left + (targetEl.offsetWidth - elProps.width) / 2 + elProps.left;
                offsetTop = targetOffset.top - elProps.height + elProps.top;
                break;
            case Tooltip.SIDE_BOTTOM:
                offsetLeft = targetOffset.left + (targetEl.offsetWidth - elProps.width) / 2 + elProps.left;
                offsetTop = targetOffset.top + targetEl.offsetHeight + elProps.top;
                break;
            case Tooltip.SIDE_LEFT:
                offsetLeft = targetOffset.left - elProps.width + elProps.left;
                offsetTop = targetOffset.top + (targetEl.offsetHeight - elProps.height) / 2 + elProps.top;
                break;
            case Tooltip.SIDE_RIGHT:
                offsetLeft = targetOffset.left + targetEl.offsetWidth + elProps.left;
                offsetTop = targetOffset.top + (targetEl.offsetHeight - elProps.height) / 2 + elProps.top;
                break;
            }

            // check screen edges
            // disabled end screen checks because of possible scroll
            if (offsetLeft < 0) {
                offsetLeft = 0;
            }
            /* else if (offsetLeft + el.offsetWidth > window.innerWidth) {
             offsetLeft = window.innerWidth - el.offsetWidth;
             }*/
            if (offsetTop < 0) {
                offsetTop = 0;
            }
            /* else if (offsetTop + el.offsetHeight > window.innerHeight) {
             offsetTop = window.innerHeight - el.offsetHeight;
             }*/

            el.setOffset({
                left: offsetLeft,
                top: offsetTop
            });
        }

        private startHideTimeout(ms?: number) {
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

        private startShowDelay(ms?: number) {
            this.stopTimeout();
            var t = ms || this.showDelay;
            if (t > 0) {
                if (this.trigger == Tooltip.TRIGGER_HOVER) {
                    // if tooltip target element becomes disabled it doesn't generate mouse leave event
                    // so we need to check whether mouse has moved from tooltip target or not
                    //this.hideOnMouseOut();
                }
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

        private hideOnMouseOut() {
            var tooltip = this;
            var mouseMoveListener = (event: MouseEvent) => {
                var tooltipTargetHtmlElement = tooltip.targetEl.getHTMLElement();
                for (var element = event.target; element; element = (<any>element).parentNode) {
                    if (element == tooltipTargetHtmlElement) {
                        return;
                    }
                }

                tooltip.startHideTimeout();
                api.dom.Body.get().unMouseMove(mouseMoveListener);
            };

            api.dom.Body.get().onMouseMove(mouseMoveListener);
        }

        private getEventName(enter: boolean) {
            switch (this.trigger) {
            case Tooltip.TRIGGER_FOCUS:
                return enter ? "focus" : "blur";
            case Tooltip.TRIGGER_HOVER:
            default:
                return enter ? "mouseenter" : "mouseleave";
                break;
            }
        }

        private hideOtherInstances() {
            Tooltip.instances.forEach((tooltip: Tooltip) => {
                if (tooltip != this && tooltip.isVisible()) {
                    //console.log("Hiding tooltip because multiple instances are not allowed", tooltip);
                    tooltip.hide();
                }
            })
        }

        static allowMultipleInstances(allow: boolean) {
            Tooltip.multipleAllowed = allow;
        }

        static isMultipleInstancesAllowed(): boolean {
            return Tooltip.multipleAllowed;
        }

    }

}