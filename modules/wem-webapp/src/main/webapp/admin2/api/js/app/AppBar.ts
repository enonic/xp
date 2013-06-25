module api_app {

    export class AppBar extends api_dom.DivEl {

        ext;

        private appName:string;

        private actions:AppBarActions;

        private launcherButton:api_dom.ButtonEl;

        private homeButton:api_dom.ButtonEl;

        private tabMenu:AppBarTabMenu;

        private userButton:UserButton;

        private userInfoPopup:UserInfoPopup;

        constructor(appName, actions:AppBarActions, tabMenu?:AppBarTabMenu) {
            super('AppBar', 'appbar');

            this.appName = appName;
            this.actions = actions;
            this.tabMenu = tabMenu;

            this.launcherButton = new LauncherButton(actions.showAppLauncherAction);
            this.appendChild(this.launcherButton);

            var separator = new Separator();
            this.appendChild(separator);

            this.homeButton = new HomeButton(this.appName, actions.showAppBrowsePanelAction);
            this.appendChild(this.homeButton);

            this.userButton = new UserButton();
            this.appendChild(this.userButton);

            if (this.tabMenu != null) {
                this.appendChild(this.tabMenu);
            }
            else {
                this.appendChild(new TabMenuContainer())
            }

            this.userInfoPopup = new UserInfoPopup();

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

        getTabMenu():AppBarTabMenu {
            return this.tabMenu;
        }
    }

    export interface AppBarActions {

        showAppLauncherAction:api_ui.Action;

        showAppBrowsePanelAction:api_ui.Action;
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

    export class TabMenuContainer extends api_dom.DivEl {

        constructor() {
            super('TabMenuContainer', 'tabmenu-container');
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