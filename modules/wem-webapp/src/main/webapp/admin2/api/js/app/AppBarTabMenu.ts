module api_app{

    export class AppBarTabMenu extends api_ui_tab.TabMenu {

        private tabMenuButton:AppBarTabMenuButton;

        constructor(idPrefix?:string) {
            super(idPrefix || "AppBarTabMenu");
            this.getEl().addClass("appbar-tabmenu");
        }

        showMenu() {
            super.showMenu();
            this.updateMenuPosition();
        }

        createTabMenuButton():AppBarTabMenuButton {
            this.tabMenuButton = new AppBarTabMenuButton();
            return this.tabMenuButton;
        }

        addNavigationItem(tab:AppBarTabMenuItem) {
            super.addNavigationItem(tab);

            this.tabMenuButton.setTabCount(this.countVisible());
            this.tabMenuButton.setEditing(tab.isEditing());

            if (this.isShowingMenuItems()) {
                this.updateMenuPosition();
            }
        }

        removeNavigationItem(tab:AppBarTabMenuItem) {
            super.removeNavigationItem(tab);

            this.tabMenuButton.setTabCount(this.countVisible());
            var newSelectedTab = <AppBarTabMenuItem>this.getSelectedNavigationItem();
            if (newSelectedTab) {
                this.tabMenuButton.setEditing(newSelectedTab.isEditing());
            }

            if (this.isShowingMenuItems()) {
                this.updateMenuPosition();
            }
        }

        getNavigationItemById(itemId:string):AppBarTabMenuItem {
            var items:api_ui_tab.TabMenuItem[] = this.getNavigationItems();
            var item;
            for (var i = 0; i < items.length; i++) {
                item = <AppBarTabMenuItem>items[i];
                if (item.getItemId() == itemId) {
                    return item;
                }
            }
            return null;
        }

        selectNavigationItem(tabIndex:number) {
            super.selectNavigationItem(tabIndex);
            var tab:api_ui_tab.TabMenuItem = this.getNavigationItem(tabIndex);
            this.tabMenuButton.setEditing((<AppBarTabMenuItem>tab).isEditing());
        }

        deselectNavigationItem() {
            super.deselectNavigationItem();
            this.tabMenuButton.setEditing(false);
            this.updateMenuPosition();
        }

        /*
         * Aligns tab items list to the center of the tab menu button
         */
        private updateMenuPosition() {
            var containerWidth = this.getEl().getWidth();
            var menuWidth = this.getMenuEl().getEl().getWidth();
            var containerPaddingLeft = this.getEl().getPaddingLeft();

            this.getMenuEl().getEl().setMarginLeft((containerWidth - menuWidth) / 2 - containerPaddingLeft + 'px');
        }
    }
}
