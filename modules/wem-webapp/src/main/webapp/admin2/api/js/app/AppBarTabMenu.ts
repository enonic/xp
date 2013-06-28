module api_app{

    export class AppBarTabMenu extends api_ui_tab.TabMenu {

        private tabMenuButton:AppBarTabMenuButton;

        constructor(idPrefix?:string) {
            super(idPrefix || "AppBarTabMenu");
            this.getEl().addClass("appbar-tabmenu");
        }

        addNavigationItem(tab:api_ui.PanelNavigationItem) {
            super.addNavigationItem(tab);
            this.tabMenuButton.setTabCount(this.countVisible());
        }

        createTabMenuButton():api_ui_tab.TabMenuButton {
            this.tabMenuButton = new AppBarTabMenuButton();
            return this.tabMenuButton;
        }

        removeNavigationItem(tab:api_ui.PanelNavigationItem) {
            super.removeNavigationItem(tab);
            this.tabMenuButton.setTabCount(this.countVisible());
        }
    }
}
