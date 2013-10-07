module api_app {

    export class ShowAppLauncherAction extends api_ui.Action {

        constructor() {
            super('Start');

            this.addExecutionListener(() => {
                new ShowAppLauncherEvent().fire();
            });
        }
    }

    export class ShowAppBrowsePanelAction extends api_ui.Action {

        constructor() {
            super('Browse');

            this.addExecutionListener(() => {
                new ShowAppBrowsePanelEvent().fire();
            });
        }
    }

    export class AppBarActions {

        public static SHOW_APP_LAUNCHER:api_ui.Action = new ShowAppLauncherAction();

        public static SHOW_APP_BROWSE_PANEL:api_ui.Action = new ShowAppBrowsePanelAction();
    }

}