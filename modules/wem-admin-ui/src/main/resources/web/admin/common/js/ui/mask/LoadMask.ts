module api.ui.mask {

    /**
     * Object to mask an Element with a splash
     */
    export class LoadMask extends Mask {

        private splash: api.dom.DivEl;

        private spinner: api.dom.DivEl;

        private text: api.dom.SpanEl;

        constructor(el: api.dom.Element) {
            super(el);
            this.addClass("load-mask");

            this.splash = new api.dom.DivEl("mask-splash");
            this.spinner = new api.dom.DivEl("spinner");
            this.splash.appendChild(this.spinner);

            this.appendChild(this.splash);
        }

        show() {
            super.show();
            this.splash.show();
            this.centerSplash();
        }

        hide() {
            this.splash.hide();
            super.hide();
        }

        setText(text: string) {
            if (!text) {
                if (this.text) {
                    this.text.hide();
                }
            } else {
                if (!this.text) {
                    this.text = new api.dom.SpanEl("text");
                    this.splash.appendChild(this.text);
                }
                this.text.getEl().setInnerHtml(text);
            }
        }

        getText(): string {
            return this.text.getEl().getInnerHtml();
        }

        private centerSplash() {
            var loaderEl = this.splash.getEl();
            loaderEl.setMarginLeft("-" + loaderEl.getWidthWithBorder() / 2 + "px");
            loaderEl.setMarginTop("-" + loaderEl.getHeightWithBorder() / 2 + "px");
        }
    }

}