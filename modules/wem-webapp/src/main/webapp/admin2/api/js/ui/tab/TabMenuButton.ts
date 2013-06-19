module api_ui_tab {

    export class TabMenuButton extends api_dom.DivEl {

        private labelEl:api_dom.SpanEl;

        private tabMenu:TabMenu;

        constructor(idPrefix?:string) {
            super(idPrefix || "TabMenuButton");

            this.labelEl = new api_dom.SpanEl();
            this.appendChild(this.labelEl);
        }

        setTabMenu(tabMenu:TabMenu) {
            this.tabMenu = tabMenu;
        }

        setLabel(value:string) {
            jQuery(<any>this.labelEl.getHTMLElement()).text(value);
        }
    }
}
