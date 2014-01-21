module api.app {

    export class NameAndIconViewBuilder {

        size: NameAndIconViewSize;

        public setSize(size: NameAndIconViewSize): NameAndIconViewBuilder {
            this.size = size;
            return this;
        }

        public build(): NameAndIconView {

            return new NameAndIconView(this);
        }
    }

    export class NameAndIconView extends api.dom.DivEl {

        private iconEl: api.dom.ImgEl;

        private mainNameEl: api.dom.H6El;

        private subNameEl: api.dom.PEl;

        constructor(builder: NameAndIconViewBuilder) {
            super("name-and-icon-view");
            var sizeClassName: string = NameAndIconViewSize[builder.size];
            this.addClass(sizeClassName);

            this.iconEl = new api.dom.ImgEl();
            this.iconEl.setClass("icon");
            this.appendChild(this.iconEl);

            var namesContainer = new api.dom.DivEl();
            namesContainer.addClass("names-container");
            this.appendChild(namesContainer);

            this.mainNameEl = new api.dom.H6El("main-name");
            namesContainer.appendChild(this.mainNameEl);

            this.subNameEl = new api.dom.PEl("sub-name");
            namesContainer.appendChild(this.subNameEl);
        }

        setMainName(value: string): NameAndIconView {

            this.mainNameEl.setText(value);
            this.mainNameEl.getEl().setAttribute("title", value);
            return this;
        }

        setSubName(value: string): NameAndIconView {

            this.subNameEl.setText(value);
            this.subNameEl.getEl().setAttribute("title", value);
            return this;
        }

        setIconUrl(value: string): NameAndIconView {

            this.iconEl.setSrc(value);
            return this;
        }
    }
}