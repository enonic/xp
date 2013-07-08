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
            var totalOffset = 0;
            this.getParent().getChildren().forEach((child) => {
                if (child != this) {
                    totalOffset += child.getHTMLElement().offsetHeight;
                } else {
                    this.getEl().setTopPx(totalOffset);
                }
            })
        }
    }

}