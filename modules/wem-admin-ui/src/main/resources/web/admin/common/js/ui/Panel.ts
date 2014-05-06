module api.ui {

    export class Panel extends api.dom.DivEl {

        private doOffset:boolean;

        constructor(className?:string) {
            super("panel" + (className ? " " + className : ""));
            this.doOffset = true;

            this.onShown((event) => {
                if(this.doOffset) {
                    this.calculateOffset();
                }
            });

            this.onRendered((event) => {
                if(this.doOffset) {
                    this.calculateOffset();
                }
            });
        }

        setDoOffset(value:boolean) {
            this.doOffset = value;
        }

        setScrollY() {
            this.addClass("scroll-y");
        }

        private calculateOffset() {
            // calculates bottom of previous element in dom and set panel top to this value.
            var previous = this.getEl().getPrevious();
            var top = previous ? (previous.getOffsetTopRelativeToParent() + previous.getHeight()) : 0;

            this.getEl().setTopPx(top);
        }
    }

}