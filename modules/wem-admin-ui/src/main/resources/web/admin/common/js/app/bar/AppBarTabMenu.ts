module api.app.bar {

    export class AppBarTabMenu extends api.ui.tab.TabMenu {

        private appBarTabMenuButton: AppBarTabMenuButton;

        private buttonLabelChangedListeners: {():void}[] = [];

        constructor() {
            super("appbar-tab-menu");
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

        showMenu() {
            super.showMenu();
            this.updateMenuPosition();
        }

        addNavigationItem(tab: AppBarTabMenuItem) {
            super.addNavigationItem(tab);

            this.appBarTabMenuButton.setTabCount(this.countVisible());
            this.appBarTabMenuButton.setEditing(tab.isEditing());
        }

        removeNavigationItem(tab: AppBarTabMenuItem) {
            super.removeNavigationItem(tab);

            this.appBarTabMenuButton.setTabCount(this.countVisible());
            var newSelectedTab = <AppBarTabMenuItem>this.getSelectedNavigationItem();
            if (newSelectedTab) {
                this.appBarTabMenuButton.setEditing(newSelectedTab.isEditing());
            }
        }

        getNavigationItemById(tabId: AppBarTabId): AppBarTabMenuItem {
            var items: api.ui.tab.TabMenuItem[] = this.getNavigationItems();
            var item;
            for (var i = 0; i < items.length; i++) {
                if (item) {
                    item = <AppBarTabMenuItem>items[i];
                    if (item.getTabId().equals(tabId)) {
                        return item;
                    }
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

        /*
         * Aligns tab items list to the center of the tab menu button
         */
        updateMenuPosition() {
            var containerWidth = this.getEl().getWidth();
            var menuWidth = this.getMenuEl().getEl().getWidth();

            this.getMenuEl().getEl().setMarginLeft((containerWidth - menuWidth) / 2 + 'px');
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
