module api.app {

    export class ShowAppLauncherAction extends api.ui.Action {

        constructor() {
            super('Start');

            this.addExecutionListener(() => {
                new ShowAppLauncherEvent().fire();
            });
        }
    }

    export class ShowAppBrowsePanelAction extends api.ui.Action {

        constructor() {
            super('Browse');

            this.addExecutionListener(() => {
                new ShowAppBrowsePanelEvent().fire();
            });
        }
    }

    export class AppBarActions {

        public static SHOW_APP_LAUNCHER:api.ui.Action = new ShowAppLauncherAction();

        public static SHOW_APP_BROWSE_PANEL:api.ui.Action = new ShowAppBrowsePanelAction();
    }

}