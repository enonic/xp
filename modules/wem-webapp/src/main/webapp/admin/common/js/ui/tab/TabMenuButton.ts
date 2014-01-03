module api.ui.tab {

    export class TabMenuButton extends api.dom.DivEl {

        private labelEl:api.dom.SpanEl;

        constructor(idPrefix?:string) {
            super(idPrefix || "TabMenuButton", "tab-menu-button");

            this.labelEl = new api.dom.SpanEl(null, 'label');
            this.appendChild(this.labelEl);
        }

        setLabel(value:string) {
            this.labelEl.getEl().setInnerHtml(value);
            this.labelEl.getEl().setAttribute('title', value);
        }
    }
}
