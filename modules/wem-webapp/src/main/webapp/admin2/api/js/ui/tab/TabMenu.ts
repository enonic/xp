module api_ui_tab {

    export class TabMenu extends api_dom.DivEl implements TabNavigator {

        ext;

        private tabMenuButton:TabMenuButton;

        private menuEl:api_dom.UlEl;

        private showingMenuItems:bool = false;

        private tabs:TabMenuItem[] = [];

        private selectedTab:number;

        private tabSelectedListeners:Function[] = [];

        private tabRemovedListeners:Function[] = [];

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

        addTab(tab:api_ui_tab.Tab) {

            var tabMenuItem = <TabMenuItem>tab;

            tabMenuItem.setTabMenu(this);

            var newLength = this.tabs.push(tabMenuItem);
            tabMenuItem.setTabIndex(newLength - 1);

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

        getSelectedTab():Tab {
            return this.tabs[this.selectedTab];
        }

        removeTab(tab:api_ui_tab.Tab) {
            var tabMenuItem = <TabMenuItem>tab;

            tabMenuItem.getEl().remove();
            var isLastTab = this.isLastTab(tab);
            this.tabs.splice(tab.getTabIndex(), 1);
            if (this.isSelectedTab(tab)) {
                if (this.isEmpty()) {
                    this.selectedTab = -1;
                } else if (tab.getTabIndex() > this.tabs.length - 1) {
                    this.selectedTab = tab.getTabIndex() - 1;
                }
            }
            if (!isLastTab) {
                for (var i = tab.getTabIndex() - 1; i < this.tabs.length; i++) {
                    this.tabs[i].setTabIndex(i);
                }
            }

            if (this.countVisible() == 0) {
                this.tabMenuButton.setLabel("");
                this.tabMenuButton.hide();
                this.hideMenu();
            }
        }

        private isSelectedTab(tab:Tab) {
            return tab.getTabIndex() == this.selectedTab;
        }

        private isLastTab(tab:Tab):bool {
            return tab.getTabIndex() === this.tabs.length;
        }

        private updateActiveTab(tabIndex:number) {
            this.tabs.forEach((tab, index) => {
                var activate = (tabIndex == index);
                tab.setActive(activate);
            });
        }

        selectTab(tabIndex:number) {
            var selectedTab = this.tabs[tabIndex];
            this.tabMenuButton.setLabel(selectedTab.getLabel());
            this.selectedTab = tabIndex;
            this.updateActiveTab(tabIndex);
        }

        getActiveTab():api_ui_tab.Tab {
            return this.getSelectedTab();
        }

        deselectTab() {
            this.tabMenuButton.setLabel("");
            this.selectedTab = -1;
        }

        addTabSelectedListener(listener:(Tab) => void) {
            this.tabSelectedListeners.push(listener);
        }

        addTabRemoveListener(listener:(Tab) => bool) {
            this.tabRemovedListeners.push(listener);
        }

        handleTabClickedEvent(tabMenuItem:TabMenuItem) {
            this.hideMenu();
            this.fireTabSelected(tabMenuItem);
        }

        handleTabRemoveButtonClickedEvent(tabMenuItem:TabMenuItem) {
            if (this.fireTabRemoveEvent(tabMenuItem)) {
                this.removeTab(tabMenuItem);
            }
        }

        fireTabSelected(tab:api_ui_tab.Tab) {
            for (var i = 0; i < this.tabSelectedListeners.length; i++) {
                this.tabSelectedListeners[i](tab);
            }
        }

        private fireTabRemoveEvent(tab:api_ui_tab.Tab):bool {
            for (var i = 0; i < this.tabRemovedListeners.length; i++) {
                if (!this.tabRemovedListeners[i](tab)) {
                    return false;
                }
            }
            return true;
        }
    }
}
