module api.ui.tab {

    export class TabBar extends api.dom.UlEl implements api.ui.Navigator {

        private tabs: TabBarItem[] = [];

        private selectedIndex: number = -1;

        private navigationItemAddedListeners: {(event: NavigatorEvent):void}[] = [];

        private navigationItemSelectedListeners: {(event: NavigatorEvent):void}[] = [];

        constructor(classes?: string) {
            super("tab-bar" + (!classes ? "" : " " + classes));
        }

        insertNavigationItem(tab: TabBarItem, index?: number, silent?: boolean) {
            this.tabs.splice(index, 0, tab);
            tab.setIndex(index);

            this.tabs.slice(index+1).forEach((tab:TabBarItem) => {
                tab.setIndex(tab.getIndex()+1);
            });

            this.insertChild(tab, index);
            tab.onSelected((event: TabItemEvent) => {
                this.selectNavigationItem(event.getTab().getIndex());
            });
            if (!silent) {
                this.notifyTabAddedListeners(tab);
            }
        }

        addNavigationItem(tab: TabBarItem, silent?: boolean) {
            this.insertNavigationItem(tab, this.tabs.length, silent);
        }

        removeNavigationItem(tab: TabBarItem) {
            var tabIndex = tab.getIndex();

            this.tabs.splice(tabIndex, 1);

            // update indexes for tabs that have been after the removed tab
            for (var i = tabIndex; i < this.tabs.length; i++) {
                this.tabs[i].setIndex(i);
            }

            if (this.isEmpty()) {
                // if there are no tabs than set selected index to negative value
                this.selectedIndex = -1;
            }
            else if ((this.getSize() - 1) < this.selectedIndex) {
                // if selected index is more than tabs amount set last index as selected
                this.selectedIndex = this.getSize() - 1;
            }
            else if (tabIndex < this.selectedIndex) {
                // if removed tab was before selected tab than decrement selected index
                this.selectedIndex--;
            }

            tab.remove();
        }

        selectNavigationItem(index: number, silent?: boolean) {
            if (index < 0 || index >= this.getSize() || this.selectedIndex == index) {
                return;
            }

            this.deselectNavigationItem();
            this.selectedIndex = index;
            var selectedTab = this.getSelectedNavigationItem();
            selectedTab.setActive(true);

            if (!silent) {
                this.notifyTabShownListeners(selectedTab);
            }
        }

        deselectNavigationItem() {
            if (this.selectedIndex != -1) {
                this.getSelectedNavigationItem().setActive(false);
            }

            this.selectedIndex = -1;
        }

        getNavigationItem(index: number): TabBarItem {
            return this.tabs[index];
        }

        getSelectedNavigationItem(): TabBarItem {
            return this.tabs[this.selectedIndex];
        }

        getSelectedIndex(): number {
            return this.selectedIndex;
        }

        getSize(): number {
            return this.tabs.length;
        }

        isEmpty(): boolean {
            return this.tabs.length === 0;
        }

        getNavigationItems(): TabBarItem[] {
            return this.tabs;
        }

        onNavigationItemAdded(listener: (event: NavigatorEvent) => void) {
            this.navigationItemAddedListeners.push(listener);
        }

        onNavigationItemSelected(listener: (event: NavigatorEvent) => void) {
            this.navigationItemSelectedListeners.push(listener);
        }

        onNavigationItemDeselected(listener: (event: NavigatorEvent) => void) {
            //Not used here
        }

        unNavigationItemAdded(listener: (event: NavigatorEvent) => void) {
            this.navigationItemAddedListeners =
            this.navigationItemAddedListeners.filter((currentListener: (event: NavigatorEvent)=>void) => {
                return listener != currentListener;
            });
        }

        unNavigationItemSelected(listener: (event: NavigatorEvent) => void) {
            this.navigationItemSelectedListeners =
            this.navigationItemSelectedListeners.filter((currentListener: (event: NavigatorEvent)=>void) => {
                return listener != currentListener;
            });
        }

        unNavigationItemDeselected(listener: (event: NavigatorEvent) => void) {
            //Not used here
        }

        private notifyTabAddedListeners(tab: TabBarItem) {
            this.navigationItemAddedListeners.forEach((listener: (event: NavigatorEvent)=>void) => {
                listener.call(this, new NavigatorEvent(tab));
            });
        }

        private notifyTabShownListeners(tab: TabBarItem) {
            this.navigationItemSelectedListeners.forEach((listener: (event: NavigatorEvent)=>void) => {
                listener.call(this, new NavigatorEvent(tab));
            });
        }
    }

}