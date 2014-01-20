module api.app {

    export class AppBar extends api.dom.DivEl {

        ext;

        private appName:string;

        private actions:AppBarActionsConfig;

        private launcherButton:api.dom.ButtonEl;

        private homeButton:api.dom.ButtonEl;

        private tabMenu:AppBarTabMenu;

        private userButton:UserButton;

        private userInfoPopup:UserInfoPopup;

        constructor(appName, tabMenu:AppBarTabMenu, actions?:AppBarActionsConfig) {
            super("appbar");

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

            var appManager:api.app.AppManager = api.app.AppManager.instance();
            this.launcherButton.setClickListener((event) => {
                appManager.showLauncher();
            });

            this.setBackgroundImgUrl(api.util.getRestUri('ui/background.jpg'));
        }

        getTabMenu():AppBarTabMenu {
            return this.tabMenu;
        }
    }

    export interface AppBarActionsConfig {

        showAppLauncherAction?:api.ui.Action;

        showAppBrowsePanelAction?:api.ui.Action;
    }

    export class LauncherButton extends api.dom.ButtonEl {

        constructor(action:api.ui.Action) {
            super('launcher-button');

            this.getEl().addEventListener('click', (event:Event) => {
                action.execute();
            });
        }

    }

    export class Separator extends api.dom.SpanEl {

        constructor() {
            super('appbar-separator');
        }

    }

    export class HomeButton extends api.dom.ButtonEl {

        constructor(text:string, action:api.ui.Action) {
            super('home-button');

            this.getEl().setInnerHtml(text);

            this.getEl().addEventListener('click', (event:Event) => {
                action.execute();
            });
        }

    }

    export class UserButton extends api.dom.ButtonEl {

        constructor() {
            super('user-button');

            var photoUrl = api.util.getAdminUri('common/images/tsi-profil.jpg');
            this.setIcon(photoUrl);
        }

        setIcon(photoUrl:string) {
            this.getEl().setBackgroundImage('url("' + photoUrl + '")');
        }

    }

}