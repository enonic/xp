module api.app {

    export class ShowAppLauncherAction extends api.ui.Action {

        constructor() {
            super('Start');

            this.onExecuted(() => {
                new ShowAppLauncherEvent().fire();
            });
        }
    }

    export class ShowAppBrowsePanelAction extends api.ui.Action {

        constructor() {
            super('Browse');

            this.onExecuted(() => {
                new ShowAppBrowsePanelEvent().fire();
            });
        }
    }

    export class AppBarActions {

        public static SHOW_APP_LAUNCHER: api.ui.Action = new ShowAppLauncherAction();

        public static SHOW_APP_BROWSE_PANEL: api.ui.Action = new ShowAppBrowsePanelAction();
    }

}