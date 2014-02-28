module api.ui.tab {

    export class TabMenu extends api.dom.DivEl implements api.ui.DeckPanelNavigator {

        private tabMenuButton: TabMenuButton;

        private menuEl: api.dom.UlEl;

        private showingMenuItems: boolean = false;

        private tabs: TabMenuItem[] = [];

        private selectedTab: number;

        private navigationItemAddedListeners: {(event: DeckPanelNavigatorEvent):void}[] = [];

        private navigationItemSelectedListeners: {(event: DeckPanelNavigatorEvent):void}[] = [];

        private navigationItemDeselectedListeners: {(event: DeckPanelNavigatorEvent):void}[] = [];


        constructor(className?: string) {
            super("tab-menu" + (className ? " " + className : ""));

            this.tabMenuButton = this.createTabMenuButton();
            this.tabMenuButton.hide();
            this.tabMenuButton.addClass("tab-menu-button");
            this.tabMenuButton.getEl().addEventListener("click", (evt:Event) => this.toggleMenu());
            this.appendChild(this.tabMenuButton);

            this.menuEl = this.createMenu();
            this.appendChild(this.menuEl);

            document.addEventListener('click', (evt:Event) => {
                this.hideMenuOnOutsideClick(evt);
            });
        }

        createTabMenuButton(): TabMenuButton {
            var btn = new TabMenuButton();
            return btn;
        }

        setButtonLabel(value: string) {
            this.tabMenuButton.setLabel(value);
        }

        setButtonClass(cls: string) {
            this.tabMenuButton.addClass(cls);
        }

        getMenuEl(): api.dom.UlEl {
            return this.menuEl;
        }

        createMenu(): api.dom.UlEl {
            var ulEl = new api.dom.UlEl();
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

        addNavigationItem(tab: TabMenuItem) {
            var newLength = this.tabs.push(tab);
            tab.setIndex(newLength - 1);

            if (tab.isVisibleInMenu()) {
                this.menuEl.appendChild(tab);
                this.tabMenuButton.show();
            }

            tab.onSelected((event: TabMenuItemSelectedEvent) => {
                this.selectNavigationItem(event.getTab().getIndex());
            });
            tab.onLabelChanged((event: TabMenuItemLabelChangedEvent) => {
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
            this.setButtonLabel("[Select]");
            this.updateActiveTab(this.selectedTab);

            this.notifyTabDeselectedListeners(selectedTab);
        }

        onNavigationItemAdded(listener: (event: DeckPanelNavigatorEvent) => void) {
            this.navigationItemAddedListeners.push(listener);
        }

        onNavigationItemSelected(listener: (event: DeckPanelNavigatorEvent) => void) {
            this.navigationItemSelectedListeners.push(listener);
        }

        onNavigationItemDeselected(listener: (event: DeckPanelNavigatorEvent) => void) {
            this.navigationItemDeselectedListeners.push(listener);
        }

        unNavigationItemAdded(listener: (event: DeckPanelNavigatorEvent) => void) {
            this.navigationItemAddedListeners =
            this.navigationItemAddedListeners.filter((currentListener: (event: DeckPanelNavigatorEvent)=>void) => {
                return listener != currentListener;
            });
        }

        unNavigationItemSelected(listener: (event: DeckPanelNavigatorEvent) => void) {
            this.navigationItemSelectedListeners =
            this.navigationItemSelectedListeners.filter((currentListener: (event: DeckPanelNavigatorEvent)=>void) => {
                return listener != currentListener;
            });
        }

        unNavigationItemDeselected(listener: (event: DeckPanelNavigatorEvent) => void) {
            this.navigationItemDeselectedListeners =
            this.navigationItemDeselectedListeners.filter((currentListener: (event: DeckPanelNavigatorEvent)=>void) => {
                return listener != currentListener;
            });
        }

        private notifyTabAddedListeners(tab: TabMenuItem) {
            this.navigationItemAddedListeners.forEach((listener: (event: DeckPanelNavigatorEvent)=>void) => {
                listener.call(this, new DeckPanelNavigatorEvent(tab));
            });
        }

        private notifyTabSelectedListeners(tab: TabMenuItem) {
            this.navigationItemSelectedListeners.forEach((listener: (event: DeckPanelNavigatorEvent)=>void) => {
                listener.call(this, new DeckPanelNavigatorEvent(tab));
            });
        }

        private notifyTabDeselectedListeners(tab: TabMenuItem) {
            this.navigationItemDeselectedListeners.forEach((listener: (event: DeckPanelNavigatorEvent)=>void) => {
                listener.call(this, new DeckPanelNavigatorEvent(tab));
            });
        }

        private hideMenuOnOutsideClick(evt: Event): void {
            var id = this.tabMenuButton.getId();
            var target: any = evt.target;
            for (var element = target; element; element = element.parentNode) {
                if (element.id === id) {
                    return; // menu clicked
                }
            }
            // click outside menu
            this.hideMenu();
        }
    }
}
