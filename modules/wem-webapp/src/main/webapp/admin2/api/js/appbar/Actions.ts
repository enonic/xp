module api_appbar {

    export class OpenStartMenuAction extends api_ui.Action {

        constructor() {
            super('Start');

            this.addExecutionListener(() => {
                new api_appbar.OpenStartMenuEvent().fire();
                console.log('api_appbar.OpenStartMenuEvent fired.');
            });
        }
    }

    export class OpenHomePageAction extends api_ui.Action {

        constructor() {
            super('Home');

            this.addExecutionListener(() => {
                new api_appbar.OpenHomePageEvent().fire();
                console.log('api_appbar.OpenHomePageEvent fired.');
            });
        }

    }

    export class ToggleUserInfoAction extends api_ui.Action {

        constructor() {
            super('UserInfo');

            this.addExecutionListener(() => {
                new api_appbar.ToggleUserInfoEvent().fire();
            });
        }

    }

    export class AppBarActions {

        static OPEN_START_MENU:api_ui.Action = new OpenStartMenuAction();
        static OPEN_HOME_PAGE:api_ui.Action = new OpenHomePageAction();
        static SHOW_USER_INFO:api_ui.Action = new ToggleUserInfoAction();

    }

}