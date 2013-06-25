module app {

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

    export class SpaceAppBarActions {

        static SHOW_APP_LAUNCHER:api_ui.Action = new ShowAppLauncherAction();

        static SHOW_APP_BROWSER_PANEL:api_ui.Action = new ShowAppBrowsePanelAction();
    }

}