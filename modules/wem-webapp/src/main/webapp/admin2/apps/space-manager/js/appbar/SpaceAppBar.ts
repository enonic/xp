module app_appbar {

    export class SpaceAppBar extends api_appbar.AppBar {

        constructor() {
            super("Space Admin",
                {
                    showAppLauncherAction: SpaceAppBarActions.SHOW_APP_LAUNCHER,
                    showAppBrowsePanelAction: SpaceAppBarActions.SHOW_APP_BROWSER_PANEL
                },
                new app_appbar.SpaceAppBarTabMenu());
        }

    }

}