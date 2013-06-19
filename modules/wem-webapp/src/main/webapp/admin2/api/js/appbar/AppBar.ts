module api_appbar {

    export class AppBar extends api_ui.DivEl {

        ext;

        private appName:string;

        private actions:AppBarActions;

        private launcherButton:api_ui.ButtonEl;

        private homeButton:api_ui.ButtonEl;

        private tabMenu:api_appbar.AppBarTabMenu;

        private userButton:api_appbar.UserButton;

        private userInfoPopup:api_appbar.UserInfoPopup;

        constructor(appName, actions:AppBarActions, tabMenu?:AppBarTabMenu) {
            super('AppBar', 'appbar');

            this.appName = appName;
            this.actions = actions;
            this.tabMenu = tabMenu;

            this.launcherButton = new api_appbar.LauncherButton(actions.showAppLauncherAction);
            this.appendChild(this.launcherButton);

            var separator = new api_appbar.Separator();
            this.appendChild(separator);

            this.homeButton = new api_appbar.HomeButton(this.appName, actions.showAppBrowsePanelAction);
            this.appendChild(this.homeButton);

            this.userButton = new api_appbar.UserButton();
            this.appendChild(this.userButton);

            if (this.tabMenu != null) {
                this.appendChild(this.tabMenu);
            }
            else {
                this.appendChild(new TabMenuContainer())
            }

            this.userInfoPopup = new api_appbar.UserInfoPopup();

            this.userButton.getEl().addEventListener('click', (event:Event) => {
                this.userInfoPopup.toggle();
            });

            this.initExt();
        }

        private initExt() {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl,
                cls: 'appbar-container',
                region: 'north'
            });
        }

        getTabMenu():api_appbar.AppBarTabMenu {
            return this.tabMenu;
        }
    }

    export interface AppBarActions {

        showAppLauncherAction:api_ui.Action;

        showAppBrowsePanelAction:api_ui.Action;
    }

    export class LauncherButton extends api_ui.ButtonEl {

        constructor(action:api_ui.Action) {
            super('LauncherButton', 'launcher-button');

            this.getEl().addEventListener('click', (event:Event) => {
                action.execute();
            });
        }

    }

    export class Separator extends api_ui.SpanEl {

        constructor() {
            super('AppBarSeparator', 'appbar-separator');
        }

    }

    export class HomeButton extends api_ui.ButtonEl {

        constructor(text:string, action:api_ui.Action) {
            super('HomeButton', 'home-button');

            this.getEl().setInnerHtml(text);

            this.getEl().addEventListener('click', (event:Event) => {
                action.execute();
            });
        }

    }

    export class TabMenuContainer extends api_ui.DivEl {

        constructor() {
            super('TabMenuContainer', 'tabmenu-container');
        }

    }

    export class UserButton extends api_ui.ButtonEl {

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