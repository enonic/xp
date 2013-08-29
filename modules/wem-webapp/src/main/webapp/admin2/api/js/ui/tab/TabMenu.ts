module api_ui_tab {

    export class TabMenu extends api_dom.DivEl implements api_ui.DeckPanelNavigator {

        ext;

        private tabMenuButton:TabMenuButton;

        private menuEl:api_dom.UlEl;

        private showingMenuItems:bool = false;

        private tabs:TabMenuItem[] = [];

        private selectedTab:number;

        constructor(idPrefix?:string) {
            super(idPrefix || "TabMenu");
            this.addClass("tab-menu");

            this.tabMenuButton = this.createTabMenuButton();
            this.tabMenuButton.hide();
            this.tabMenuButton.getEl().addEventListener("click", () => {
                this.toggleMenu();
            });
            this.appendChild(this.tabMenuButton);

            this.menuEl = this.createMenu();
            this.appendChild(this.menuEl);

        }

        createTabMenuButton():TabMenuButton {
            var btn = new TabMenuButton();
            btn.addClass("tab-menu-button");
            return btn;
        }

        setButtonLabel(value:string) {
            this.tabMenuButton.setLabel(value);
        }

        getMenuEl(): api_dom.UlEl {
            return this.menuEl;
        }

        createMenu():api_dom.UlEl {
            var ulEl = new api_dom.UlEl();
            ulEl.getEl().setZindex(19001);
            ulEl.getEl().setPosition("absolute");
            ulEl.hide();
            return ulEl;
        }

        private toggleMenu() {
            if (!this.showingMenuItems) {
                this.showMenu();
            } else {
                this.hideMenu();
            }
        }

        hideMenu() {
            this.menuEl.hide();
            this.showingMenuItems = false;
        }

        showMenu() {
            this.menuEl.show();
            this.showingMenuItems = true;
        }

        isShowingMenuItems(): bool {
            return this.showingMenuItems;
        }

        addNavigationItem(tab:api_ui_tab.TabMenuItem) {

            tab.setTabMenu(this);

            var newLength = this.tabs.push(tab);
            tab.setIndex(newLength - 1);

            if (tab.isVisible()) {
                // TODO: Why is this done?
                //this.tabMenuButton.setLabel(tab.getLabel());
                this.menuEl.appendChild(tab);
                this.tabMenuButton.show();
            }
        }

        isEmpty():bool {
            return this.tabs.length == 0;
        }

        getSize():number {
            return this.tabs.length;
        }

        countVisible():number {
            var size = 0;
            this.tabs.forEach((tab:TabMenuItem) => {
                if (tab.isVisible()) {
                    size++;
                }
            });
            return size;
        }

        getSelectedTabIndex():number {
            return this.selectedTab;
        }

        getSelectedNavigationItem():api_ui_tab.TabMenuItem {
            return this.tabs[this.selectedTab];
        }

        getNavigationItem(tabIndex:number):api_ui_tab.TabMenuItem {
            return this.tabs[tabIndex];
        }

        getNavigationItems():api_ui_tab.TabMenuItem[] {
            return this.tabs;
        }

        removeNavigationItem(tab:api_ui_tab.TabMenuItem) {
            var tabMenuItem = <TabMenuItem>tab;

            tabMenuItem.getEl().remove();
            var isLastTab = this.isLastTab(tab);
            this.tabs.splice(tab.getIndex(), 1);
            if (this.isSelectedTab(tab)) {
                if (this.isEmpty()) {
                    this.selectedTab = -1;
                } else if (tab.getIndex() > this.tabs.length - 1) {
                    this.selectedTab = tab.getIndex() - 1;
                }
            }
            if (!isLastTab) {
                for (var i = tab.getIndex() - 1; i < this.tabs.length; i++) {
                    this.tabs[i].setIndex(i);
                }
            }

            if (this.countVisible() == 0) {
                this.tabMenuButton.setLabel("");
                this.tabMenuButton.hide();
                this.hideMenu();
            } else {
                var newTab = this.getNavigationItem(this.selectedTab);
                if (newTab) {
                    this.tabMenuButton.setLabel(newTab.getLabel());
                }
            }
        }

        private isSelectedTab(tab:api_ui.PanelNavigationItem) {
            return tab.getIndex() == this.selectedTab;
        }

        private isLastTab(tab:api_ui.PanelNavigationItem):bool {
            return tab.getIndex() === this.tabs.length;
        }

        private updateActiveTab(tabIndex:number) {
            this.tabs.forEach((tab, index) => {
                var activate = (tabIndex == index);
                tab.setActive(activate);
            });
        }

        selectNavigationItem(tabIndex:number) {
            var selectedTab = this.tabs[tabIndex];
            this.tabMenuButton.setLabel(selectedTab.getLabel());
            this.selectedTab = tabIndex;
            this.updateActiveTab(tabIndex);
        }

        deselectNavigationItem() {
            this.tabMenuButton.setLabel("");
            this.selectedTab = -1;
        }
    }
}
