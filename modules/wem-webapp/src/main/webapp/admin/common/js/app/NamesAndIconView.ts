module api.app {

    export class NamesAndIconViewBuilder {

        size: NamesAndIconViewSize;

        public setSize(size: NamesAndIconViewSize): NamesAndIconViewBuilder {
            this.size = size;
            return this;
        }

        public build(): NamesAndIconView {

            return new NamesAndIconView(this);
        }
    }

    export class NamesAndIconView extends api.dom.DivEl {

        private wrapperDivEl: api.dom.DivEl;

        private iconImageEl: api.dom.ImgEl;

        private iconDivEl: api.dom.DivEl;

        private namesView: api.app.NamesView;

        private iconLabelEl: api.dom.SpanEl;

        constructor(builder: NamesAndIconViewBuilder) {
            super("names-and-icon-view");
            var sizeClassName: string = NamesAndIconViewSize[builder.size];
            if (builder.size) {
                this.addClass(sizeClassName);
            }

            this.wrapperDivEl = new api.dom.DivEl("wrapper");
            this.appendChild(this.wrapperDivEl);

            this.iconImageEl = new api.dom.ImgEl(null,"icon");
            this.wrapperDivEl.appendChild(this.iconImageEl);

            this.iconDivEl = new api.dom.DivEl("icon");
            this.wrapperDivEl.appendChild(this.iconDivEl);
            this.iconDivEl.hide();

            this.namesView = new api.app.NamesView();
            this.appendChild(this.namesView);

            this.iconLabelEl = new api.dom.SpanEl("icon-label");
            this.iconLabelEl.hide();
            this.appendChild(this.iconLabelEl);
        }

        setMainName(value: string): NamesAndIconView
        {
            this.namesView.setMainName(value);
            return this;
        }

        setSubName(value: string): NamesAndIconView
        {
            this.namesView.setSubName(value);
            return this;
        }

        setIconClass(value: string): NamesAndIconView {
            this.iconDivEl.setClass("icon " + value);
            this.iconDivEl.show();
            this.iconImageEl.hide();
            return this;
        }

        setIconUrl(value: string): NamesAndIconView
        {
            this.iconImageEl.setSrc(value + '?thumbnail=false&size=64');
            this.iconDivEl.hide();
            this.iconImageEl.show();
            return this;
        }

        setDisplayIconLabel(display:boolean): NamesAndIconView {
            if (display) {
                this.iconLabelEl.show();
            } else {
                this.iconLabelEl.hide();
            }

            return this;
        }
    }
}