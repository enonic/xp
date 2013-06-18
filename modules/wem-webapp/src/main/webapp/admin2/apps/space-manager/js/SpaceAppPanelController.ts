module app {

    export class SpaceAppPanelController extends api_ui_tab.TabPanelController {

        constructor() {

            var appBarTabMenu:api_ui_tab.TabNavigator = new api_ui_tab.TabMenu();
            var appPanel:api_ui.DeckPanel = new api_ui.DeckPanel();

            super(appBarTabMenu, appPanel);

            app_event.NewSpaceEvent.on((event) => {

                var tabMenuItem = new app_appbar.SpaceTabMenuItem("New Space");
                var panel = new api_ui.Panel();
                this.addPanel( panel, tabMenuItem );
            });

            app_event.OpenSpaceEvent.on((event) => {

                var tabMenuItem = new app_appbar.SpaceTabMenuItem("TODO");
                var panel = new api_ui.Panel();
                this.addPanel( panel, tabMenuItem );
            });
        }
    }
}
