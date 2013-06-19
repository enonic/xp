module api_ui_tab {

    export class TabMenu extends api_ui.DivEl implements TabNavigator {

        ext;

        private tabMenuButton:TabMenuButton;

        private menuEl:api_ui.UlEl;

        private showingMenuItems:bool = false;

        private tabs:TabMenuItem[] = [];

        private selectedTab:TabMenuItem;

        private tabSelectedListeners:Function[] = [];

        private tabRemovedListeners:Function[] = [];

        constructor(idPrefix?:string) {
            super(idPrefix || "TabMenu");

            this.tabMenuButton = this.createTabMenuButton();
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

        createMenu():api_ui.UlEl {
            var ulEl = new api_ui.UlEl();
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

            this.tabMenuButton.setLabel(tab.getLabel());

            this.menuEl.appendChild(tabMenuItem);

        }

        getSize():number {
            return this.tabs.length;
        }

        removeTab(tab:api_ui_tab.Tab) {
            var tabMenuItem = <TabMenuItem>tab;

            tabMenuItem.getEl().remove();
            var isLastTab = this.isLastTab(tab);
            this.tabs.splice(tab.getTabIndex(), 1);
            if( this.isSelectedTab( tab ) )
            {
                // TODO: update selected tab acccoridnt to same logic as in DeckPanel.remove
            }
            if (!isLastTab) {
                for (var i = tab.getTabIndex() - 1; i < this.tabs.length; i++) {
                    this.tabs[i].setTabIndex(i);
                }
            }
        }

        private isSelectedTab(tab:Tab) {
            return tab == this.selectedTab;
        }

        private isLastTab(tab:Tab):bool {
            return tab.getTabIndex() === this.tabs.length;
        }

        selectTab(tab:api_ui_tab.Tab) {
            this.tabMenuButton.setLabel(tab.getLabel());
            this.selectedTab = <TabMenuItem>tab;
        }

        getActiveTab():api_ui_tab.Tab {
            return this.selectedTab;
        }

        deselectTab() {
            this.tabMenuButton.setLabel("");
            this.selectedTab = null;
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
