module api_ui_tab {

    export class TabMenuButton extends api_ui.DivEl {

        private labelEl:api_ui.SpanEl;

        private tabMenu:TabMenu;

        constructor(idPrefix?:string) {
            super(idPrefix || "TabMenuButton");

            this.labelEl = new api_ui.SpanEl();
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
