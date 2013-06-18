module app_appbar {

    export class ContentAppBar extends api_appbar.AppBar {

        constructor() {
            super("Content Manager", {
                showAppLauncherAction: ContentAppBarActions.SHOW_APP_LAUNCHER,
                showAppBrowsePanelAction: ContentAppBarActions.SHOW_APP_BROWSER_PANEL
            });
        }

    }

}