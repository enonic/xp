module api_ui_tab {

    export class TabMenuItem extends api_dom.LiEl implements Tab {

        private tabIndex:number;

        private label:string;

        private labelEl:api_dom.SpanEl;

        private tabMenu:TabMenu;

        constructor(label:string) {
            super("TabMenuItem", "tab-menu-item");

            this.label = label;
            this.labelEl = new api_dom.SpanEl();
            this.labelEl.getEl().setInnerHtml(label);
            this.appendChild(this.labelEl);

            var removeButton = new api_dom.ButtonEl();
            removeButton.getEl().setInnerHtml("X");
            this.appendChild(removeButton);

            this.labelEl.getEl().addEventListener("click", () => {
                this.tabMenu.handleTabClickedEvent(this);
            });

            removeButton.getEl().addEventListener("click", () => {
                this.tabMenu.handleTabRemoveButtonClickedEvent(this);
            });
        }

        setTabMenu(tabMenu:TabMenu) {
            this.tabMenu = tabMenu;
        }

        setTabIndex(value:number) {
            this.tabIndex = value;
        }

        getTabIndex():number {
            return this.tabIndex;
        }

        getLabel():string {
            return this.label;
        }

    }
}
