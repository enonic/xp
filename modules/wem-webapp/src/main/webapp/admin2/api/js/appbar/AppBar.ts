module api_appbar {

    export class AppBar extends api_ui.DivEl {

        ext;

        appName:string;

        private startButton:api_ui.ButtonEl;
        private homeButton:api_ui.ButtonEl;
        private tabMenu:api_appbar.TabMenuContainer;
        private userButton:api_appbar.UserButton;
        private userInfoPopup:api_appbar.UserInfoPopup;

        constructor(appName) {
            super('AppBar', 'appbar');
            this.appName = appName;

            this.addStartButton();
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

        private addStartButton() {
            this.startButton = new api_appbar.StartButton();
            this.appendChild(this.startButton);
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

            api_event.onEvent(api_appbar.ToggleUserInfoEvent.NAME, () => {
                this.userInfoPopup.toggle();
            });
        }
    }

    export class StartButton extends api_ui.ButtonEl {

        constructor() {
            super('StartButton', 'start-button');

            this.getEl().addEventListener('click', (event:Event) => {
                api_appbar.AppBarActions.OPEN_START_MENU.execute();
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
                api_appbar.AppBarActions.OPEN_HOME_PAGE.execute();
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

            this.getEl().addEventListener('click', (event:Event) => {
                api_appbar.AppBarActions.SHOW_USER_INFO.execute();
            });
        }

        setIcon(photoUrl:string) {
            this.getEl().setBackgroundImage('url("' + photoUrl + '")');
        }

    }

}