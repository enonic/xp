module api_appbar {

    export class AppBar extends api_ui.DivEl {

        ext;

        appName:string;

        private launcherButton:api_ui.ButtonEl;
        private homeButton:api_ui.ButtonEl;
        private tabMenu:api_appbar.TabMenuContainer;
        private userButton:api_appbar.UserButton;
        private userInfoPopup:api_appbar.UserInfoPopup;

        constructor(appName) {
            super('AppBar', 'appbar');
            this.appName = appName;

            this.addLauncherButton();
            this.addSeparator();
            this.addHomeButton();
            this.addUserButton();
            this.addTabMenu();
            this.addUserInfoPopup();

            this.initExt();
        }

        private initExt() {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl
            });
        }

        private addLauncherButton() {
            this.launcherButton = new api_appbar.LauncherButton();
            this.appendChild(this.launcherButton);
        }

        private addSeparator() {
            var separator = new api_appbar.Separator();
            this.appendChild(separator);
        }

        private addHomeButton() {
            this.homeButton = new api_appbar.HomeButton(this.appName);
            this.appendChild(this.homeButton);
        }

        private addTabMenu() {
            this.tabMenu = new api_appbar.TabMenuContainer();
            this.appendChild(this.tabMenu);
        }

        private addUserButton() {
            this.userButton = new api_appbar.UserButton();
            this.appendChild(this.userButton);
        }

        private addUserInfoPopup() {
            this.userInfoPopup = new api_appbar.UserInfoPopup();

            this.userButton.getEl().addEventListener('click', (event:Event) => {
                this.userInfoPopup.toggle();
            });
        }
    }

    export class LauncherButton extends api_ui.ButtonEl {

        constructor() {
            super('LauncherButton', 'launcher-button');

            this.getEl().addEventListener('click', (event:Event) => {
                api_appbar.AppBarActions.OPEN_APP_LAUNCHER.execute();
            });
        }

    }

    export class Separator extends api_ui.SpanEl {

        constructor() {
            super('AppBarSeparator', 'appbar-separator');
        }

    }

    export class HomeButton extends api_ui.ButtonEl {

        constructor(text:string) {
            super('HomeButton', 'home-button');

            this.getEl().setInnerHtml(text);

            this.getEl().addEventListener('click', (event:Event) => {
                api_appbar.AppBarActions.SHOW_APP_BROWSER_PANEL.execute();
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