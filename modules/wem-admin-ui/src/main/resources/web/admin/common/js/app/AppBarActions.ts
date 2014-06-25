module api.app {

    export class ShowAppLauncherAction extends api.ui.Action {

        constructor(application: api.app.Application) {
            super('Start', 'esc');

            this.onExecuted(() => {
                new ShowAppLauncherEvent(application).fire(window.parent);
                new ShowAppLauncherEvent(application).fire();
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

        public static SHOW_APP_BROWSE_PANEL: api.ui.Action = new ShowAppBrowsePanelAction();
    }

}