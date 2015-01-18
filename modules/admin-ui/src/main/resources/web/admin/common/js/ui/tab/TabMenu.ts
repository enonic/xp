module api.ui.tab {

    export class TabMenu extends api.dom.DivEl implements api.ui.Navigator {

        private tabMenuButton: TabMenuButton;

        private menuEl: api.dom.UlEl;

        private menuVisible: boolean = false;

        private tabs: TabMenuItem[] = [];

        private selectedTab: number;

        private hideOnItemClick: boolean = true;

        private navigationItemAddedListeners: {(event: NavigatorEvent):void}[] = [];

        private navigationItemRemovedListeners: {(event: NavigatorEvent):void}[] = [];

        private navigationItemSelectedListeners: {(event: NavigatorEvent):void}[] = [];

        private navigationItemDeselectedListeners: {(event: NavigatorEvent):void}[] = [];

        private enabled: boolean = true;

        constructor(className?: string) {
            super("tab-menu" + (className ? " " + className : ""));

            this.tabMenuButton = this.createTabMenuButton();
            this.tabMenuButton.hide();
            this.tabMenuButton.addClass("tab-menu-button");
            this.tabMenuButton.onClicked((event: MouseEvent) => {
                if (this.enabled) {
                    this.toggleMenu();
                }
            });
            this.appendChild(this.tabMenuButton);

            this.menuEl = new api.dom.UlEl("menu");
            this.appendChild(this.menuEl);

            api.dom.Body.get().onClicked((event: MouseEvent) => new HideTabMenuEvent(this).fire());
            HideTabMenuEvent.on((event) => {
                if (event.getTabMenu() !== this) {
                    this.hideMenu();
                }
            });

            this.onClicked((e: MouseEvent) => {
                if (this.enabled) {
                    // menu itself was clicked so do nothing
                    e.preventDefault();
                    e.stopPropagation();
                    new HideTabMenuEvent(this).fire();
                }
            });
        }

        setEnabled(enabled: boolean): TabMenu {
            this.enabled = enabled;
            this.toggleClass('disabled', !enabled);
            return this;
        }

        createTabMenuButton(): TabMenuButton {
            return new TabMenuButton();
        }

        setButtonLabel(value: string): TabMenu {
            this.tabMenuButton.setLabel(value);
            return this;
        }

        setButtonClass(cls: string): TabMenu {
            this.tabMenuButton.addClass(cls);
            return this;
        }

        setHideOnItemClick(hide: boolean): TabMenu {
            this.hideOnItemClick = hide;
            return this;
        }

        getTabMenuButtonEl(): TabMenuButton {
            return this.tabMenuButton;
        }

        getMenuEl(): api.dom.UlEl {
            return this.menuEl;
        }

        private toggleMenu() {
            if (!this.menuVisible) {
                this.showMenu();
            } else {
                this.hideMenu();
            }
        }

        hideMenu() {
            this.menuEl.hide();
            this.menuVisible = false;
            this.removeClass('expanded');
        }

        showMenu() {
            this.menuEl.show();
            this.menuVisible = true;
            this.addClass('expanded');
        }

        isMenuVisible(): boolean {
            return this.menuVisible;
        }

        addNavigationItem(tab: TabMenuItem) {
            var newLength = this.tabs.push(tab);
            tab.setIndex(newLength - 1);

            if (tab.isVisibleInMenu()) {
                this.menuEl.appendChild(tab);
                this.tabMenuButton.show();
            }
            this.initializeNewItemEvents(tab);
        }

        prependNavigationItem(tab: TabMenuItem) {
            this.tabs.unshift(tab);
            this.tabs.forEach((curTab: TabMenuItem) => {
                curTab.setIndex(curTab.getIndex() + 1);
            });
            tab.setIndex(0);

            if (tab.isVisibleInMenu()) {
                this.menuEl.prependChild(tab);
                this.tabMenuButton.show();
            }

            this.initializeNewItemEvents(tab);
        }

        private initializeNewItemEvents(tab: TabMenuItem) {
            tab.onSelected((event: TabItemSelectedEvent) => {
                this.selectNavigationItem(event.getTab().getIndex());
                if (this.hideOnItemClick) {
                    this.hideMenu();
                }
            });
            tab.onLabelChanged((event: TabItemLabelChangedEvent) => {
                this.setButtonLabel(event.getNewValue());
            });

            this.notifyTabAddedListeners(tab);
        }


        isEmpty(): boolean {
            return this.tabs.length == 0;
        }

        getSize(): number {
            return this.tabs.length;
        }

        countVisible(): number {
            var size = 0;
            this.tabs.forEach((tab: TabMenuItem) => {
                if (tab.isVisibleInMenu()) {
                    size++;
                }
            });
            return size;
        }

        getSelectedNavigationItem(): TabMenuItem {
            return this.tabs[this.selectedTab];
        }

        getSelectedIndex(): number {
            return this.selectedTab;
        }

        getNavigationItem(tabIndex: number): TabMenuItem {
            return this.tabs[tabIndex];
        }

        getNavigationItems(): TabMenuItem[] {
            return this.tabs;
        }

        removeNavigationItem(tab: TabMenuItem) {
            tab.remove();

            this.tabs.splice(tab.getIndex(), 1);

            if (this.isEmpty()) {
                // if there are no tabs set selected index to negative value
                this.selectedTab = -1;
            } else if (tab.getIndex() < this.selectedTab) {
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
                this.setButtonLabel("");
                this.tabMenuButton.hide();
                this.hideMenu();
            } else {
                var newTab = this.getSelectedNavigationItem();
                if (newTab) {
                    this.setButtonLabel(newTab.getLabel());
                }
            }
            this.notifyTabRemovedListeners(tab);
        }

        removeNavigationItems() {
            this.tabs.forEach((tab: TabMenuItem) => {
                this.removeNavigationItem(tab);
            });
            this.tabs = [];
        }

        private updateActiveTab(tabIndex: number) {
            this.tabs.forEach((tab: TabMenuItem, index: number) => {
                var activate = (tabIndex == index);
                tab.setActive(activate);
            });
        }

        selectNavigationItem(tabIndex: number) {
            if (tabIndex < 0 || tabIndex >= this.getSize() || this.selectedTab == tabIndex) {
                return;
            }

            this.selectedTab = tabIndex;
            var selectedTab = this.getNavigationItem(tabIndex);
            this.setButtonLabel(selectedTab.getLabel());
            this.updateActiveTab(tabIndex);

            this.notifyTabSelectedListeners(selectedTab);
        }

        deselectNavigationItem() {
            var selectedTab = this.getSelectedNavigationItem();
            this.selectedTab = -1;
            this.updateActiveTab(this.selectedTab);

            this.notifyTabDeselectedListeners(selectedTab);
        }

        onNavigationItemAdded(listener: (event: NavigatorEvent) => void) {
            this.navigationItemAddedListeners.push(listener);
        }

        onNavigationItemRemoved(listener: (event: NavigatorEvent) => void) {
            this.navigationItemRemovedListeners.push(listener);
        }

        onNavigationItemSelected(listener: (event: NavigatorEvent) => void) {
            this.navigationItemSelectedListeners.push(listener);
        }

        onNavigationItemDeselected(listener: (event: NavigatorEvent) => void) {
            this.navigationItemDeselectedListeners.push(listener);
        }

        unNavigationItemAdded(listener: (event: NavigatorEvent) => void) {
            this.navigationItemAddedListeners =
            this.navigationItemAddedListeners.filter((currentListener: (event: NavigatorEvent)=>void) => {
                return listener != currentListener;
            });
        }

        unNavigationItemRemoved(listener: (event: NavigatorEvent) => void) {
            this.navigationItemRemovedListeners =
            this.navigationItemRemovedListeners.filter((currentListener: (event: NavigatorEvent)=>void) => {
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
            this.navigationItemDeselectedListeners =
            this.navigationItemDeselectedListeners.filter((currentListener: (event: NavigatorEvent)=>void) => {
                return listener != currentListener;
            });
        }

        private notifyTabAddedListeners(tab: TabMenuItem) {
            this.navigationItemAddedListeners.forEach((listener: (event: NavigatorEvent)=>void) => {
                listener.call(this, new NavigatorEvent(tab));
            });
        }

        private notifyTabRemovedListeners(tab: TabMenuItem) {
            this.navigationItemRemovedListeners.forEach((listener: (event: NavigatorEvent)=>void) => {
                listener.call(this, new NavigatorEvent(tab));
            });
        }

        private notifyTabSelectedListeners(tab: TabMenuItem) {
            this.navigationItemSelectedListeners.forEach((listener: (event: NavigatorEvent)=>void) => {
                listener.call(this, new NavigatorEvent(tab));
            });
        }

        private notifyTabDeselectedListeners(tab: TabMenuItem) {
            this.navigationItemDeselectedListeners.forEach((listener: (event: NavigatorEvent)=>void) => {
                listener.call(this, new NavigatorEvent(tab));
            });
        }
    }
}
