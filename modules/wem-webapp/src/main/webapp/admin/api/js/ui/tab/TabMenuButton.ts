module api_ui_tab {

    export class TabMenuButton extends api_dom.DivEl {

        private labelEl:api_dom.SpanEl;

        constructor(idPrefix?:string) {
            super(idPrefix || "TabMenuButton", "tab-menu-button");

            this.labelEl = new api_dom.SpanEl(null, 'label');
            this.appendChild(this.labelEl);
        }

        setLabel(value:string) {
            this.labelEl.getEl().setInnerHtml(value);
            this.labelEl.getEl().setAttribute('title', value);
        }
    }
}
