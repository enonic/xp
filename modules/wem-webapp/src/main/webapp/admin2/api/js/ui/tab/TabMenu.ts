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

            this.tabMenuButton = this.createTabMenuButton();
            this.tabMenuButton.hide();
            this.tabMenuButton.getEl().addEventListener("click", () => {
                this.toggleMenu();
            });
            this.appendChild(this.tabMenuButton);

            this.menuEl = this.createMenu();
            this.appendChild(this.menuEl);

            this.initExt();
        }

        createTabMenuButton():TabMenuButton {
            return new TabMenuButton();
        }

        createMenu():api_dom.UlEl {
            var ulEl = new api_dom.UlEl();
            ulEl.getEl().setZindex(19001);
            ulEl.hide();
            return ulEl;
        }

        private initExt() {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl
            });
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

        addNavigationItem(tab:api_ui.PanelNavigationItem) {

            var tabMenuItem = <TabMenuItem>tab;

            tabMenuItem.setTabMenu(this);

            var newLength = this.tabs.push(tabMenuItem);
            tabMenuItem.setIndex(newLength - 1);

            if (tab.isVisible()) {
                this.tabMenuButton.setLabel(tab.getLabel());
                this.menuEl.appendChild(tabMenuItem);
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

        getSelectedNavigationItem():api_ui.PanelNavigationItem {
            return this.tabs[this.selectedTab];
        }

        getNavigationItem(tabIndex:number) {
            return this.tabs[tabIndex];
        }

        removeNavigationItem(tab:api_ui.PanelNavigationItem) {
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
