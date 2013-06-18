module api_ui_tab {

    export class TabMenuItem extends api_ui.LiEl implements Tab {

        private tabIndex:number;

        private label:string;

        private labelEl:api_ui.SpanEl;

        private tabMenu:TabMenu;

        constructor(label:string) {
            super("TabMenuItem", "tab-menu-item");

            this.label = label;
            this.labelEl = new api_ui.SpanEl();
            this.labelEl.getEl().setInnerHtml(label);
            this.appendChild(this.labelEl);

            var removeButton = new api_ui.ButtonEl();
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
