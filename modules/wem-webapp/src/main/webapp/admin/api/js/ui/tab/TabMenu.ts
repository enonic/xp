module api_ui_tab {

    export class TabMenu extends api_dom.DivEl implements api_ui.DeckPanelNavigator {

        private tabMenuButton:TabMenuButton;

        private menuEl:api_dom.UlEl;

        private showingMenuItems:boolean = false;

        private tabs:TabMenuItem[] = [];

        private selectedTab:number;

        private listeners:api_ui.DeckPanelNavigatorListener[] = [];

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

        isShowingMenuItems(): boolean {
            return this.showingMenuItems;
        }

        addNavigationItem(tab:TabMenuItem) {
            var newLength = this.tabs.push(tab);
            tab.setIndex(newLength - 1);

            if (tab.isVisibleInMenu()) {
                this.menuEl.appendChild(tab);
                this.tabMenuButton.show();
            }

            tab.addListener({
                onSelected: (tab:TabMenuItem) => {
                    this.selectNavigationItem(tab.getIndex());
                },
                onLabelChanged: (newValue:string, oldValue:string) => {
                    this.setButtonLabel(newValue);
                }
            });

            this.notifyTabAddedListeners(tab);
        }

        isEmpty():boolean {
            return this.tabs.length == 0;
        }

        getSize():number {
            return this.tabs.length;
        }

        countVisible():number {
            var size = 0;
            this.tabs.forEach((tab:TabMenuItem) => {
                if (tab.isVisibleInMenu()) {
                    size++;
                }
            });
            return size;
        }

        getSelectedNavigationItem():TabMenuItem {
            return this.tabs[this.selectedTab];
        }

        getSelectedIndex():number {
            return this.selectedTab;
        }

        getNavigationItem(tabIndex:number):TabMenuItem {
            return this.tabs[tabIndex];
        }

        getNavigationItems():TabMenuItem[] {
            return this.tabs;
        }

        removeNavigationItem(tab:TabMenuItem) {
            tab.remove();

            this.tabs.splice(tab.getIndex(), 1);

            if (this.isEmpty()) {
                // if there are no tabs set selected index to negative value
                this.selectedTab = -1;
            } else if ( tab.getIndex() < this.selectedTab ) {
                // if removed tab was before selected tab than decrement selected index
                this.selectedTab--;
            } else if (tab.getIndex() > this.getSize() - 1) {
                // if selected index is more than tabs amount set last index as selected
                this.selectedTab = this.getSize() - 1;
            }

            // update indexes for tabs that have been after the removed tab
            for (var i = tab.getIndex(); i < this.tabs.length; i++) {
                this.tabs[i].setIndex(i);
            }

            if (this.countVisible() == 0) {
                this.tabMenuButton.setLabel("");
                this.tabMenuButton.hide();
                this.hideMenu();
            } else {
                var newTab = this.getSelectedNavigationItem();
                if (newTab) {
                    this.tabMenuButton.setLabel(newTab.getLabel());
                }
            }
        }

        private updateActiveTab(tabIndex:number) {
            this.tabs.forEach((tab:TabMenuItem, index: number) => {
                var activate = (tabIndex == index);
                tab.setActive(activate);
            });
        }

        selectNavigationItem(tabIndex:number) {
            if ( tabIndex < 0 || tabIndex >= this.getSize() || this.selectedTab == tabIndex ) {
                return;
            }

            this.selectedTab = tabIndex;
            var selectedTab = this.getNavigationItem(tabIndex);
            this.setButtonLabel(selectedTab.getLabel());
            this.updateActiveTab(tabIndex);

            this.notifyTabSelectedListeners(selectedTab);
        }

        deselectNavigationItem() {
            this.tabMenuButton.setLabel("");
            this.selectedTab = -1;
        }

        addListener(listener:api_ui.DeckPanelNavigatorListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:api_ui.DeckPanelNavigatorListener) {
            this.listeners = this.listeners.filter((elem) => {
                return elem != listener;
            });
        }

        private notifyTabAddedListeners(tab:TabMenuItem) {
            this.listeners.forEach((listener:api_ui.DeckPanelNavigatorListener) => {
                if (listener.onNavigationItemAdded) {
                    listener.onNavigationItemAdded(tab);
                }
            });
        }

        private notifyTabSelectedListeners(tab:TabMenuItem) {
            this.listeners.forEach((listener:api_ui.DeckPanelNavigatorListener) => {
                if (listener.onNavigationItemSelected) {
                    listener.onNavigationItemSelected(tab);
                }
            });
        }
    }
}
