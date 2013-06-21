module api{

    export class AppPanel extends api_ui_tab.TabbedDeckPanel {

        private homePanel:api_ui.Panel;

        constructor(appBar:api_ui_tab.TabNavigator, homePanel:api_ui.Panel) {
            super(appBar);

            this.homePanel = homePanel;
            var homePanelMenuItem = new api_appbar.AppBarTabMenuItem("home");
            homePanelMenuItem.setVisible(false);
            homePanelMenuItem.setRemovable(false);
            this.addTab(homePanelMenuItem, this.homePanel);
            this.showPanel(0);
        }

        showBrowsePanel() {
            this.showPanel(0);
        }


    }
}
