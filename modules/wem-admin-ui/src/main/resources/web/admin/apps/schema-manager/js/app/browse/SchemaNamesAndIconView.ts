module app.browse {

    export class SchemaNamesAndIconView extends api.app.NamesAndIconView {

        private overlayEl: api.dom.SpanEl;

        constructor(builder: api.app.NamesAndIconViewBuilder) {
            super(builder);
            this.overlayEl = null;
        }

        private addOverlay() {
            this.overlayEl = new api.dom.SpanEl('overlay');
            this.overlayEl.insertAfterEl(this.getIconImageEl());
        }

        setOverlay(overlayClass: string) {
            if (!this.overlayEl) {
                this.addOverlay();
            }
            this.getWrapperDivEl().setClass('wrapper overlay-wrapper').addClass(overlayClass);
        }
    }
}