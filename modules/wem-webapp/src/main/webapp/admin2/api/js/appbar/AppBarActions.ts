module api_appbar {

    export class OpenAppLauncherAction extends api_ui.Action {

        constructor() {
            super('Start');

            this.addExecutionListener(() => {
                new api_appbar.OpenAppLauncherEvent().fire();
                console.log('api_appbar.OpenAppLauncherEvent fired.');
            });
        }
    }

    export class ShowAppBrowsePanelAction extends api_ui.Action {

        constructor() {
            super('Home');

            this.addExecutionListener(() => {
                new api_appbar.ShowAppBrowsePanelEvent().fire();
                console.log('api_appbar.ShowAppBrowsePanelEvent fired.');
            });
        }

    }

    export class AppBarActions {

        static OPEN_APP_LAUNCHER:api_ui.Action = new OpenAppLauncherAction();
        static SHOW_APP_BROWSER_PANEL:api_ui.Action = new ShowAppBrowsePanelAction();

    }

}