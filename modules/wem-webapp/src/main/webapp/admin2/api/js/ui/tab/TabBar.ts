module api_ui_tab {

    export class TabBar extends api_dom.UlEl implements api_ui.DeckPanelNavigator {

        private tabs: TabBarItem[] = [];

        private selectedIndex: number = -1;

        private listeners: api_ui.DeckPanelNavigatorListener[] = [];

        constructor(idPrefix?:string, className?:string) {
            super(idPrefix || "TabBar", className || "tab-bar");
        }

        addNavigationItem(tab:TabBarItem) {
            var newLength = this.tabs.push(tab);
            tab.setIndex(newLength - 1);

            this.appendChild(tab);

            tab.addListener({
                onSelected: (tab:TabBarItem) => {
                    this.selectNavigationItem(tab.getIndex());
                }
            });

            this.notifyTabAddedListeners(tab);
        }

        removeNavigationItem(tab:TabBarItem) {
            var tabIndex = tab.getIndex();

            this.tabs.splice(tabIndex, 1);

            // update indexes for tabs that have been after the removed tab
            for ( var i = tabIndex; i < this.tabs.length; i++) {
                this.tabs[i].setIndex(i);
            }

            if ( this.isEmpty() ) {
                // if there are no tabs than set selected index to negative value
                this.selectedIndex = -1;
            }
            else if ( (this.getSize() - 1) < this.selectedIndex ) {
                // if selected index is more than tabs amount set last index as selected
                this.selectedIndex = this.getSize() - 1;
            }
            else if ( tabIndex < this.selectedIndex ) {
                // if removed tab was before selected tab than decrement selected index
                this.selectedIndex--;
            }

            tab.remove();
        }

        selectNavigationItem(index:number) {
            if ( index < 0 || index >= this.getSize() || this.selectedIndex == index ) {
                return;
            }

            this.deselectNavigationItem();
            this.selectedIndex = index;
            var selectedTab = this.getSelectedNavigationItem();
            selectedTab.setActive(true);

            this.notifyTabShownListeners(selectedTab);
        }

        deselectNavigationItem() {
            if (this.selectedIndex != -1) {
                this.getSelectedNavigationItem().setActive(false);
            }

            this.selectedIndex = -1;
        }

        getNavigationItem(index:number):TabBarItem {
            return this.tabs[index];
        }

        getSelectedNavigationItem():TabBarItem {
            return this.tabs[this.selectedIndex];
        }

        getSelectedIndex():number {
            return this.selectedIndex;
        }

        getSize():number {
            return this.tabs.length;
        }

        isEmpty():boolean {
            return this.tabs.length === 0;
        }

        getNavigationItems():TabBarItem[] {
            return this.tabs;
        }

        addListener(listener:api_ui.DeckPanelNavigatorListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:api_ui.DeckPanelNavigatorListener) {
            this.listeners = this.listeners.filter((elem) => {
                return elem != listener;
            });
        }

        private notifyTabAddedListeners(tab:TabBarItem) {
            this.listeners.forEach((listener:api_ui.DeckPanelNavigatorListener) => {
                if (listener.onNavigationItemAdded) {
                    listener.onNavigationItemAdded(tab);
                }
            });
        }

        private notifyTabShownListeners(tab:TabBarItem) {
            this.listeners.forEach((listener:api_ui.DeckPanelNavigatorListener) => {
                if (listener.onNavigationItemSelected) {
                    listener.onNavigationItemSelected(tab);
                }
            });
        }
    }

}