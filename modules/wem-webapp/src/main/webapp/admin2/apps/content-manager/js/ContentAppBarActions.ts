module app_appbar {

    export class ShowAppLauncherAction extends api_ui.Action {

        constructor() {
            super('Start');

            this.addExecutionListener(() => {
                new api_app.ShowAppLauncherEvent().fire();
            });
        }
    }

    export class ShowAppBrowsePanelAction extends api_ui.Action {

        constructor() {
            super('Browse');

            this.addExecutionListener(() => {
                new api_app.ShowAppBrowsePanelEvent().fire();
            });
        }
    }

    export class ContentAppBarActions {

        static SHOW_APP_LAUNCHER:api_ui.Action = new app_appbar.ShowAppLauncherAction();

        static SHOW_APP_BROWSER_PANEL:api_ui.Action = new ShowAppBrowsePanelAction();
    }

}