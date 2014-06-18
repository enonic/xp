module api.ui {

    export class ResponsiveItem {

        // Global responsive item properties
        private static sizeRanges = [
            ResponsiveRanges._0_240,    // none (necessary for valid check)
            ResponsiveRanges._240_360,     // mobile vertical
            ResponsiveRanges._360_540,    // mobile horizontal
            ResponsiveRanges._540_720,    // Phablet
            ResponsiveRanges._720_960,    // Tablet vertical
            ResponsiveRanges._960_1200,    // Tablet horizontal
            ResponsiveRanges._1200_1380,    // 13"
            ResponsiveRanges._1380_1620,    // 15"
            ResponsiveRanges._1620_1920,    // TV
            ResponsiveRanges._1920_UP // Monitor
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
