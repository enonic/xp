module api.app {

    export class AppBarTabMenu extends api.ui.tab.TabMenu {

        private appBarTabMenuButton: AppBarTabMenuButton;

        private buttonLabelChanged: {():void}[] = [];

        constructor() {
            super("appbar-tabmenu");
        }

        createTabMenuButton():AppBarTabMenuButton {
            this.appBarTabMenuButton = new AppBarTabMenuButton();
            return this.appBarTabMenuButton;
        }

        createMenu(): api.dom.UlEl {
            var menu = super.createMenu();
            menu.getEl().setPosition('fixed');
            return menu;
        }

        setButtonLabel(value: string) {
            super.setButtonLabel(value);
            this.notifyButtonLabelChanged();
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

        getNavigationItemById(tabId:AppBarTabId):AppBarTabMenuItem {
            var items:api.ui.tab.TabMenuItem[] = this.getNavigationItems();
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

        /*
         * Aligns tab items list to the center of the tab menu button
         */
        updateMenuPosition() {
            var fullWidth = api.dom.Body.get().getEl().getWidth();
            var tabEl = this.getEl();
            var menuEl = this.getMenuEl().getEl();

            var tabCenterOffsetRight = fullWidth - tabEl.getOffsetLeft() - tabEl.getWidthWithMargin() / 2;
            menuEl.setRight(Math.max(tabCenterOffsetRight - menuEl.getWidthWithBorder() / 2, 0)  + 'px');
            menuEl.setWidth('auto').setWidth(fullWidth < menuEl.getWidthWithBorder() ? fullWidth + 'px' : 'auto');
        }

        onButtonLabelChanged(listener: () => void) {
            this.buttonLabelChanged.push(listener);
        }

        unButtonLabelChanged(listener: () => void) {
            this.buttonLabelChanged = this.buttonLabelChanged.filter((currentListener: () => void) => {
                return listener != currentListener;
            });
        }

        private notifyButtonLabelChanged() {
            this.buttonLabelChanged.forEach((listener: () => void) => {
                listener.call(this);
            });
        }
    }
}
