module api.ui.panel {

    /**
     * Use Panel when you need a container that needs 100% height.
     */
    export class Panel extends api.dom.DivEl {

        private doOffset: boolean;

        constructor(className?: string) {
            super("panel" + (className ? " " + className : ""));
            this.doOffset = true;

            this.onShown((event) => {
                if (this.doOffset) {
                    this.calculateOffset();
                }
            });
        }

        setDoOffset(value: boolean) {
            this.doOffset = value;

            if (value && this.isRendered()) {
                this.calculateOffset();
            }
        }

        private calculateOffset() {
            // calculates bottom of previous element in dom and set panel top to this value.
            var previous = this.getEl().getPrevious();
            var top = previous ? (previous.getOffsetTopRelativeToParent() + previous.getHeightWithMargin()) : 0;

            this.getEl().setTopPx(top);
        }
    }

}