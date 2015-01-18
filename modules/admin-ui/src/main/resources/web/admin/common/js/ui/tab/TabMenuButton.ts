module api.ui.tab {

    export class TabMenuButton extends api.dom.DivEl {

        private labelEl: api.dom.SpanEl;

        constructor() {
            super("tab-menu-button icon-arrow-down2");

            this.labelEl = new api.dom.SpanEl('label');
            this.appendChild(this.labelEl);
        }

        setLabel(value: string) {
            this.labelEl.getEl().setInnerHtml(value);
            this.labelEl.getEl().setAttribute('title', value);
        }
    }
}
