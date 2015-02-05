module api.app {

    export class NamesView extends api.dom.DivEl {

        private mainNameEl: api.dom.H6El;

        private subNameEl: api.dom.PEl;

        private addTitleAttribute: boolean;

        constructor(addTitleAttribute: boolean = true) {
            super("names-view");

            this.addTitleAttribute = addTitleAttribute

            this.mainNameEl = new api.dom.H6El("main-name");
            this.appendChild(this.mainNameEl);

            this.subNameEl = new api.dom.PEl("sub-name");
            this.appendChild(this.subNameEl);
        }

        setMainName(value: string): NamesView {
            this.mainNameEl.getEl().setText(value);
            if (this.addTitleAttribute) {
                this.mainNameEl.getEl().setAttribute("title", value);
            }
            return this;
        }

        setSubName(value: string, title?: string): NamesView {
            this.subNameEl.getEl().setText(value);
            if (this.addTitleAttribute) {
                this.subNameEl.getEl().setAttribute("title", title || value);
            }
            return this;
        }
    }
}