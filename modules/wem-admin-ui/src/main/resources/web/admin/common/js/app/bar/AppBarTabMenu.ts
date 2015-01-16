module api.app.bar {

    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;

    import TabMenuItem = api.ui.tab.TabMenuItem;

    export class AppBarTabMenu extends api.ui.tab.TabMenu {

        static TAB_WIDTH: number = 190;

        static MAX_WIDTH: number = 248; // maximum width, when only one tab shown

        private appBarTabMenuButton: AppBarTabMenuButton;

        private barEl: api.dom.UlEl;

        private buttonLabelChangedListeners: {():void}[] = [];

        private timeoutHandler: number;

        constructor() {
            super("appbar-tab-menu");
            this.barEl = new api.dom.UlEl("bar");
            this.prependChild(this.barEl);

            this.onRendered(() => {
                this.updateTabMenuButtonVisibility();
            });

            ResponsiveManager.onAvailableSizeChanged(this, this.moveTabs.bind(this, 50));
        }

        private updateTabMenuButtonVisibility() {
            var menuTabsCount = this.getMenuEl().getChildren().length;
            if (menuTabsCount === 0) {
                this.appBarTabMenuButton.hide();
                this.getMenuEl().hide();
            } else {
                this.appBarTabMenuButton.show();
            }
            this.appBarTabMenuButton.setTabCount(menuTabsCount);
        }

        private moveTabs(timeout: number = 0) {
            clearInterval(this.timeoutHandler);
            this.timeoutHandler = setTimeout(() => {
                var width = this.getEl().getWidth(),
                    barWidth = this.barEl.getEl().getWidth(),
                    exactTabs = AppBarTabMenu.MAX_WIDTH < width ? Math.ceil(barWidth / AppBarTabMenu.TAB_WIDTH) || 1 : 1,
                    barTabs = this.barEl.getChildren(),
                    menuTabs = this.getMenuEl().getChildren(),
                    tabsInBar = barTabs.length,
                    tabsInMenu = menuTabs.length;

                while (!(
                        // escape condition
                        exactTabs === tabsInBar ||
                        (exactTabs > tabsInBar && tabsInMenu === 0)
                    )) {

                    if (exactTabs > tabsInBar) {
                        if (tabsInMenu > 0) {
                            var tabEl = this.getMenuEl().getFirstChild();
                            this.getMenuEl().removeChild(tabEl);
                            this.barEl.appendChild(tabEl);
                            tabsInBar++;
                            tabsInMenu--;
                        }
                    } else if (exactTabs < tabsInBar) {
                        if (tabsInBar > 0) {
                            var tabEl = this.barEl.getLastChild();
                            this.barEl.removeChild(tabEl);
                            this.getMenuEl().prependChild(tabEl);
                            tabsInBar--;
                            tabsInMenu++;
                        }
                    }

                }
                this.updateTabMenuButtonVisibility();
            }, timeout);
        }

        createTabMenuButton(): AppBarTabMenuButton {
            this.appBarTabMenuButton = new AppBarTabMenuButton();
            return this.appBarTabMenuButton;
        }

        setButtonLabel(value: string): AppBarTabMenu {
            super.setButtonLabel(value);
            this.notifyButtonLabelChanged();
            return this;
        }

        addNavigationItem(tab: AppBarTabMenuItem) {
            super.addNavigationItem(tab);

            this.appBarTabMenuButton.setTabCount(this.countVisible());
            this.appBarTabMenuButton.setEditing(tab.isEditing());

            this.moveTabs(0);
        }

        removeNavigationItem(tab: AppBarTabMenuItem) {
            super.removeNavigationItem(tab);

            this.appBarTabMenuButton.setTabCount(this.countVisible());
            var newSelectedTab = <AppBarTabMenuItem>this.getSelectedNavigationItem();
            if (newSelectedTab) {
                this.appBarTabMenuButton.setEditing(newSelectedTab.isEditing());
            }

            this.moveTabs(0);
        }

        getNavigationItemById(tabId: AppBarTabId): AppBarTabMenuItem {
            var items: api.ui.tab.TabMenuItem[] = this.getNavigationItems();
            var item;
            for (var i = 0; i < items.length; i++) {
                item = <AppBarTabMenuItem>items[i];
                if (item.getTabId().equals(tabId)) {
                    return item;
                }
            }
            return null;
        }

        selectNavigationItem(tabIndex: number) {
            super.selectNavigationItem(tabIndex);
            var tab = <AppBarTabMenuItem>this.getNavigationItem(tabIndex);
            this.appBarTabMenuButton.setEditing(tab.isEditing());

            this.hideMenu();
        }

        deselectNavigationItem() {
            super.deselectNavigationItem();
            this.appBarTabMenuButton.setEditing(false);
        }

        onButtonLabelChanged(listener: () => void) {
            this.buttonLabelChangedListeners.push(listener);
        }

        unButtonLabelChanged(listener: () => void) {
            this.buttonLabelChangedListeners = this.buttonLabelChangedListeners.filter((currentListener: () => void) => {
                return listener != currentListener;
            });
        }

        private notifyButtonLabelChanged() {
            this.buttonLabelChangedListeners.forEach((listener: () => void) => {
                listener.call(this);
            });
        }
    }
}
