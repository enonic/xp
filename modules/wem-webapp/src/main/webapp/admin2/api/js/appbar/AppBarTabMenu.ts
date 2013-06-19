module api_appbar{

    export class AppBarTabMenu extends api_ui_tab.TabMenu {

        private tabMenuButton:AppBarTabMenuButton;

        constructor(idPrefix?:string) {
            super(idPrefix || "AppBarTabMenu");
            this.getEl().addClass("appbar-tabmenu");
        }

        addTab(tab:api_ui_tab.Tab) {
            super.addTab(tab);
            this.tabMenuButton.setTabCount(this.getSize())
        }

        createTabMenuButton():api_ui_tab.TabMenuButton {
            this.tabMenuButton = new AppBarTabMenuButton();
            return this.tabMenuButton;
        }

        removeTab(tab:api_ui_tab.Tab) {
            super.removeTab(tab);

            this.tabMenuButton.setTabCount( this.getSize() );
            if( this.getSize() == 0 ) {
                this.tabMenuButton.setLabel("")
            }
        }
    }
}
