module api_ui_tab {

    export class TabMenu extends api_ui.DivEl implements TabNavigator {

        ext;

        private tabMenuButton:TabMenuButton;

        private menuEl:api_ui.UlEl;

        private showingMenuItems:bool = false;

        private tabs:TabMenuItem[] = [];

        private tabSelectedListeners:TabSelectedListener[] = [];

        private tabRemovedListeners:api_ui_tab.TabRemoveListener[] = [];

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

            this.tabs.splice(tab.getTabIndex());
        }

        selectTab(tab:api_ui_tab.Tab) {


        }

        addTabSelectedListener(listener:TabSelectedListener) {
            this.tabSelectedListeners.push(listener);
        }

        addTabRemoveListener(listener:api_ui_tab.TabRemoveListener) {
            this.tabRemovedListeners.push(listener);
        }

        handleTabClickedEvent(tabMenuItem:TabMenuItem) {
            this.hideMenu();
            this.fireTabSelected(tabMenuItem);
        }

        handleTabRemoveButtonClickedEvent(tabMenuItem:TabMenuItem) {
            this.fireBeforeTabRemoved(tabMenuItem);
        }

        fireTabSelected(tab:api_ui_tab.Tab) {
            for (var i = 0; i < this.tabSelectedListeners.length; i++) {
                this.tabSelectedListeners[i].selectedTab(tab);
            }
        }

        private fireBeforeTabRemoved(tab:api_ui_tab.Tab) {
            for (var i = 0; i < this.tabRemovedListeners.length; i++) {
                this.tabRemovedListeners[i].tabRemove(tab);
            }
        }
    }
}
