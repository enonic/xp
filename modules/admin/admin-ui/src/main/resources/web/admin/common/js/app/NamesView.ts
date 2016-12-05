module api.app {

    export class NamesView extends api.dom.DivEl {

        private mainNameEl: api.dom.H6El;

        private subNameEl: api.dom.PEl;

        private addTitleAttribute: boolean;

        constructor(addTitleAttribute: boolean = true) {
            super("names-view", api.StyleHelper.COMMON_PREFIX);

            this.addTitleAttribute = addTitleAttribute;

            this.mainNameEl = new api.dom.H6El("main-name", api.StyleHelper.COMMON_PREFIX);
            this.appendChild(this.mainNameEl);

            this.subNameEl = new api.dom.PEl("sub-name", api.StyleHelper.COMMON_PREFIX);
            this.appendChild(this.subNameEl);
        }

        setMainName(value: string, escapeHtml: boolean = true): NamesView {
            this.mainNameEl.setHtml(value, escapeHtml);
            if (this.addTitleAttribute) {
                this.mainNameEl.getEl().setAttribute("title", value);
            }
            return this;
        }

        setSubName(value: string, title?: string): NamesView {
            this.subNameEl.setHtml(value);
            if (this.addTitleAttribute) {
                this.subNameEl.getEl().setAttribute("title", title || value);
            }
            return this;
        }

        setSubNameElements(elements: api.dom.Element[]): NamesView {
            elements.forEach((element: api.dom.Element) => {
                this.subNameEl.appendChild(element);
            });

            return this;
        }
    }
}