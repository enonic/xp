module api_app {

    export class AppBar extends api_dom.DivEl {

        ext;

        private appName:string;

        private actions:AppBarActionsConfig;

        private launcherButton:api_dom.ButtonEl;

        private homeButton:api_dom.ButtonEl;

        private tabMenu:AppBarTabMenu;

        private userButton:UserButton;

        private userInfoPopup:UserInfoPopup;

        constructor(appName, tabMenu:AppBarTabMenu, actions?:AppBarActionsConfig) {
            super('AppBar', 'appbar');

            this.appName = appName;
            this.tabMenu = tabMenu;

            this.actions = <AppBarActionsConfig> {};
            this.actions.showAppLauncherAction = (actions && actions.showAppLauncherAction) || AppBarActions.SHOW_APP_LAUNCHER;
            this.actions.showAppBrowsePanelAction = (actions && actions.showAppBrowsePanelAction) || AppBarActions.SHOW_APP_BROWSE_PANEL;

            this.launcherButton = new LauncherButton(this.actions.showAppLauncherAction);
            this.appendChild(this.launcherButton);

            var separator = new Separator();
            this.appendChild(separator);

            this.homeButton = new HomeButton(this.appName, this.actions.showAppBrowsePanelAction);
            this.appendChild(this.homeButton);

            this.userButton = new UserButton();
            this.appendChild(this.userButton);

            this.appendChild(this.tabMenu);

            this.userInfoPopup = new UserInfoPopup();

            this.userButton.getEl().addEventListener('click', (event:Event) => {
                this.userInfoPopup.toggle();
            });

            var appManager:api_app.AppManager = api_app.AppManager.instance();
            this.launcherButton.setClickListener((event) => {
                appManager.showLauncher();
            });
        }

        getTabMenu():AppBarTabMenu {
            return this.tabMenu;
        }
    }

    export interface AppBarActionsConfig {

        showAppLauncherAction?:api_ui.Action;

        showAppBrowsePanelAction?:api_ui.Action;
    }

    export class LauncherButton extends api_dom.ButtonEl {

        constructor(action:api_ui.Action) {
            super('LauncherButton', 'launcher-button');

            this.getEl().addEventListener('click', (event:Event) => {
                action.execute();
            });
        }

    }

    export class Separator extends api_dom.SpanEl {

        constructor() {
            super('AppBarSeparator', 'appbar-separator');
        }

    }

    export class HomeButton extends api_dom.ButtonEl {

        constructor(text:string, action:api_ui.Action) {
            super('HomeButton', 'home-button');

            this.getEl().setInnerHtml(text);

            this.getEl().addEventListener('click', (event:Event) => {
                action.execute();
            });
        }

    }

    export class UserButton extends api_dom.ButtonEl {

        constructor() {
            super('UserButton', 'user-button');

            var photoUrl = api_util.getAbsoluteUri('admin/resources/images/tsi-profil.jpg');
            this.setIcon(photoUrl);
        }

        setIcon(photoUrl:string) {
            this.getEl().setBackgroundImage('url("' + photoUrl + '")');
        }

    }

}