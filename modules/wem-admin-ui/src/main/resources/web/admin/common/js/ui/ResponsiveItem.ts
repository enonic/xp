module api.ui {

    export class ResponsiveItem {

        // Global responsive item properties
        private static sizeRanges = [
            new ResponsiveRange(   0,  240),    // none (necessary for valid check)
            new ResponsiveRange( 240,  360),    // mobile vertical
            new ResponsiveRange( 360,  540),    // mobile horizontal
            new ResponsiveRange( 540,  720),    // Phablet
            new ResponsiveRange( 720,  960),    // Tablet vertical
            new ResponsiveRange( 960, 1200),    // Tablet horizontal
            new ResponsiveRange(1200, 1380),    // 13"
            new ResponsiveRange(1380, 1620),    // 15"
            new ResponsiveRange(1620, 1920),    // TV
            new ResponsiveRange(1920, Infinity) // Monitor
        ];


        private element: api.dom.Element;

        private rangeSize: ResponsiveRange;     // Current layoutRange with class

        private rangeValue: number;             // Range (width) value of the previous state

        private handle: Function;               // Additional handler on update

        constructor(element: api.dom.Element, handler: Function = (() => {})) {
            this.element = element;
            this.rangeValue = this.element.getEl().getWidthWithBorder();
            this.handle = handler;
            this.fitToRange();
        }

        private fitToRange() {
            for (var i = 0; i < ResponsiveItem.sizeRanges.length; i++) {
                if (ResponsiveItem.sizeRanges[i].isFit(this.rangeValue)) {
                    this.rangeSize = ResponsiveItem.sizeRanges[i];
                    break;
                }
            }
        }

        getElement(): api.dom.Element {
            return this.element;
        }

        /*
        When used with responsive layout, make sure to call this method:
        on global Window resize, on special events (stop dragging) and on shown
        (for the first time initialization).
        */
        update() {
            var newRangeValue = this.element.getEl().getWidthWithBorder();
            if (newRangeValue !== this.rangeValue) {
                this.rangeValue = newRangeValue;
                this.element.getEl().removeClass(this.rangeSize.getRangeClass());
                this.fitToRange(); // update rangeSize
                this.element.getEl().addClass(this.rangeSize.getRangeClass());
            }
            this.handle();         // Additional handler
        }

        setHandler(handler: Function = (() => {})) {
            this.handle = handler;
        }

        getRangeValue():number {
            return this.rangeValue;
        }

        getRangeSize():ResponsiveRange {
            return this.rangeSize;
        }
    }
}
