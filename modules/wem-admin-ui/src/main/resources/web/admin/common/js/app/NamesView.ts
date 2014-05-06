module api.app {

    export class NamesView extends api.dom.DivEl {

        private mainNameEl: api.dom.H6El;

        private subNameEl: api.dom.PEl;

        constructor()
        {
            super("names-view");

            this.mainNameEl = new api.dom.H6El("main-name");
            this.appendChild(this.mainNameEl);

            this.subNameEl = new api.dom.PEl("sub-name");
            this.appendChild(this.subNameEl);
        }

        setMainName(value: string): NamesView
        {
            this.mainNameEl.setText(value);
            this.mainNameEl.getEl().setAttribute("title", value);
            return this;
        }

        setSubName(value: string): NamesView
        {
            this.subNameEl.setText(value);
            this.subNameEl.getEl().setAttribute("title", value);
            return this;
        }
    }
}