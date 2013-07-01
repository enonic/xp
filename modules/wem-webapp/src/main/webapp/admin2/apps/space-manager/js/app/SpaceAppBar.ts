module app {

    export class SpaceAppBar extends api_app.AppBar {

        constructor() {
            super("Space Admin", new SpaceAppBarTabMenu(),
                {
                    showAppLauncherAction: SpaceAppBarActions.SHOW_APP_LAUNCHER,
                    showAppBrowsePanelAction: SpaceAppBarActions.SHOW_APP_BROWSER_PANEL
                });
        }

    }

}