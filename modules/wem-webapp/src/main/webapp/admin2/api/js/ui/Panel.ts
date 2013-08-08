module api_ui {

    export class Panel extends api_dom.DivEl {

        private doOffset:bool;

        constructor(idPrefix?:string) {
            super(idPrefix, "panel");
            this.doOffset = true;
        }

        afterRender() {
            if(this.doOffset) {
                this.calculateOffset();
            }
        }

        setDoOffset(value:bool) {
            this.doOffset = value;
        }

        private calculateOffset() {
            // calculates bottom of previous element in dom and set panel top to this value.
            var previous = this.getEl().getPrevious();
            var top = previous ? (previous.getOffsetTop() + previous.getHeight()) : 0;

            this.getEl().setTopPx(top);
        }
    }

}