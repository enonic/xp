module api.ui.responsive {

    export class ResponsiveItem {

        private element: api.dom.Element;

        private rangeSize: ResponsiveRange; // Current layoutRange with class

        private rangeValue: number;         // Range (width) value of the previous state

        private handle: Function;           // Additional handler on update

        constructor(element: api.dom.Element, handler: (item: ResponsiveItem) => void = ((item: ResponsiveItem) => {
        })) {
            this.element = element;
            this.rangeValue = this.element.getEl().getWidthWithBorder();
            this.handle = handler;
            this.fitToRange();
        }

        private fitToRange() {
            for (var key in ResponsiveRanges) {
                var range = ResponsiveRanges[key];
                if (range && (api.ObjectHelper.iFrameSafeInstanceOf(range, ResponsiveRange)) && range.isFit(this.rangeValue)) {
                    this.rangeSize = range;
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
            this.handle(this);     // Additional handler
        }

        setHandler(handler: (item: ResponsiveItem) => void = ((item: ResponsiveItem) => {
        })) {
            this.handle = handler;
        }

        getRangeValue(): number {
            return this.rangeValue;
        }

        getRangeSize(): ResponsiveRange {
            return this.rangeSize;
        }

        isInRange(range: ResponsiveRange): boolean {
            return range.isFit(this.rangeValue);
        }

        isInRangeOrSmaller(range: ResponsiveRange): boolean {
            return range.isFitOrSmaller(this.rangeValue);
        }

        isInRangeOrBigger(range: ResponsiveRange): boolean {
            return range.isFitOrBigger(this.rangeValue);
        }
    }
}
