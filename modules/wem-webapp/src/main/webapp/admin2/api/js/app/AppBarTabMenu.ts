module api_app{

    export class AppBarTabMenu extends api_ui_tab.TabMenu {

        private tabMenuButton:AppBarTabMenuButton;

        constructor(idPrefix?:string) {
            super(idPrefix || "AppBarTabMenu");
            this.getEl().addClass("appbar-tabmenu");
        }

        createTabMenuButton():api_ui_tab.TabMenuButton {
            this.tabMenuButton = new AppBarTabMenuButton();
            return this.tabMenuButton;
        }

        addNavigationItem(tab:api_ui.PanelNavigationItem) {
            super.addNavigationItem(tab);
            this.tabMenuButton.setTabCount(this.countVisible());
            this.tabMenuButton.setEditing((<AppBarTabMenuItem>tab).isEditing());
        }

        removeNavigationItem(tab:api_ui.PanelNavigationItem) {
            super.removeNavigationItem(tab);
            this.tabMenuButton.setTabCount(this.countVisible());
            var newTab = <AppBarTabMenuItem>this.getSelectedNavigationItem();
            if (newTab) {
                this.tabMenuButton.setEditing(newTab.isEditing());
            }

        }

        selectNavigationItem(tabIndex:number) {
            super.selectNavigationItem(tabIndex);
            var tab:api_ui_tab.TabMenuItem = this.getNavigationItem(tabIndex);
            this.tabMenuButton.setEditing((<AppBarTabMenuItem>tab).isEditing());
        }

        deselectNavigationItem() {
            super.deselectNavigationItem();
            this.tabMenuButton.setEditing(false);
        }
    }
}
