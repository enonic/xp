module api.util.htmlarea.dialog {

    export class ImageCroppingNameView extends api.dom.DivEl {

        private mainNameEl: api.dom.H6El;

        private addTitleAttribute: boolean;

        constructor(addTitleAttribute: boolean = true) {
            super("names-view");

            this.addTitleAttribute = addTitleAttribute

            this.mainNameEl = new api.dom.H6El("main-name");
            this.appendChild(this.mainNameEl);
        }

        setMainName(value: string): ImageCroppingNameView {
            this.mainNameEl.setHtml(value);
            if (this.addTitleAttribute) {
                this.mainNameEl.getEl().setAttribute("title", value);
            }
            return this;
        }
    }
}