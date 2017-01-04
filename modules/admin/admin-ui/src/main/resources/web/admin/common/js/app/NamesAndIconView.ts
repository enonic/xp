module api.app {

    export class NamesAndIconViewBuilder {

        size: NamesAndIconViewSize;

        addTitleAttribute: boolean = false;

        appendIcon: boolean = true;

        setSize(size: NamesAndIconViewSize): NamesAndIconViewBuilder {
            this.size = size;
            return this;
        }

        setAddTitleAttribute(addTitleAttribute: boolean): NamesAndIconViewBuilder {
            this.addTitleAttribute = addTitleAttribute;
            return this;
        }

        setAppendIcon(appendIcon: boolean): NamesAndIconViewBuilder {
            this.appendIcon = appendIcon;
            return this;
        }

        build(): NamesAndIconView {

            return new NamesAndIconView(this);
        }
    }

    export class NamesAndIconView extends api.dom.DivEl {

        private wrapperDivEl: api.dom.DivEl;

        private iconImageEl: api.dom.ImgEl;

        private iconDivEl: api.dom.DivEl;

        private iconEl: api.dom.Element;

        private namesView: api.app.NamesView;

        private iconLabelEl: api.dom.SpanEl;

        constructor(builder: NamesAndIconViewBuilder) {
            super("names-and-icon-view");
            let sizeClassName: string = NamesAndIconViewSize[builder.size];
            if (builder.size) {
                this.addClass(sizeClassName);
            }

            if (builder.appendIcon) {
                this.wrapperDivEl = new api.dom.DivEl("wrapper", api.StyleHelper.COMMON_PREFIX);
                this.appendChild(this.wrapperDivEl);

                this.iconImageEl = new api.dom.ImgEl(null, "font-icon-default");
                this.wrapperDivEl.appendChild(this.iconImageEl);

                this.iconDivEl = new api.dom.DivEl("font-icon-default");
                this.wrapperDivEl.appendChild(this.iconDivEl);
                this.iconDivEl.hide();
            }

            this.namesView = new api.app.NamesView(builder.addTitleAttribute);
            this.appendChild(this.namesView);

            this.iconLabelEl = new api.dom.SpanEl("icon-label", api.StyleHelper.COMMON_PREFIX);
            this.iconLabelEl.hide();
            this.appendChild(this.iconLabelEl);
        }

        setMainName(value: string): NamesAndIconView {
            this.namesView.setMainName(value);
            return this;
        }

        setSubName(value: string, title?: string): NamesAndIconView {
            this.namesView.setSubName(value, title);
            return this;
        }

        setSubNameElements(elements: api.dom.Element[]): NamesAndIconView {
            this.namesView.setSubNameElements(elements);
            return this;
        }

        setIconClass(value: string): NamesAndIconView {
            this.iconDivEl
                .setClass("font-icon-default " + value)
                .removeChildren()
                .getEl().setDisplay('inline-block');
            this.iconImageEl.hide();

            return this;
        }

        setIconUrl(value: string): NamesAndIconView {
            this.iconImageEl.setSrc(value);
            this.iconDivEl.hide();
            this.iconImageEl.show();
            return this;
        }

        setIconEl(value: api.dom.Element): NamesAndIconView {
            if (this.iconEl) {
                this.iconEl.remove();
            }
            this.iconEl = value;
            this.iconDivEl.appendChild(value).show();
            this.iconImageEl.hide();
            return this;
        }

        setDisplayIconLabel(display: boolean): NamesAndIconView {
            if (display) {
                this.iconLabelEl.show();
            } else {
                this.iconLabelEl.hide();
            }

            return this;
        }

        getNamesView(): api.app.NamesView {
            return this.namesView;
        }

        /**
         * protected, to be used by inheritors
         */
        getIconImageEl(): api.dom.ImgEl {
            return this.iconImageEl;
        }

        /**
         * protected, to be used by inheritors
         */
        getWrapperDivEl(): api.dom.DivEl {
            return this.wrapperDivEl;
        }

        setIconToolTip(toolTip: string) {
            this.wrapperDivEl.getEl().setTitle(toolTip);
        }

        static create(): NamesAndIconViewBuilder {
            return new NamesAndIconViewBuilder();
        }

    }
}