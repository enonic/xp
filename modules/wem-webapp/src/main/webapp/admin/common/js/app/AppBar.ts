module api.app {

    export class AppBar extends api.dom.DivEl {

        private appName: string;

        private actions: AppBarActionsConfig;

        private launcherButton: api.dom.ButtonEl;

        private homeButton: api.dom.ButtonEl;

        private tabMenu: AppBarTabMenu;

        private userButton: UserButton;

        private userInfoPopup: UserInfoPopup;

        constructor(appName, tabMenu: AppBarTabMenu, actions?: AppBarActionsConfig) {
            super("appbar");

            this.appName = appName;
            this.tabMenu = tabMenu;
            this.tabMenu.onNavigationItemSelected(() => {
                this.layoutChildren();
            });
            this.tabMenu.onNavigationItemDeselected(() => {
                this.layoutChildren();
            });

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
            this.userInfoPopup.hide();

            this.userButton.onClicked((event: MouseEvent) => this.userInfoPopup.toggle());

            var appManager: api.app.AppManager = api.app.AppManager.instance();
            this.launcherButton.onClicked((event: MouseEvent) => appManager.showLauncher());

            this.setBackgroundImgUrl(api.util.getRestUri('ui/background.jpg'));

            api.dom.Window.get().onResized((event: UIEvent) => this.layoutChildren());

            this.onRendered((event) => {
                this.layoutChildren();
            })
        }

        getTabMenu(): AppBarTabMenu {
            return this.tabMenu;
        }

        private layoutChildren() {
            var fullWidth = this.getEl().getWidth();

            var homeEl = this.homeButton.getEl();
            var homeElRightEdge = homeEl.getOffset().left + homeEl.getWidthWithMargin();

            var tabEl = this.tabMenu.getEl();
            var centerLeftEdge = (fullWidth - tabEl.getWidth() ) / 2;

            var tabElLeftEdge = Math.max(homeElRightEdge, centerLeftEdge);
            tabEl.setLeft(tabElLeftEdge + "px");

            var userEl = this.userButton.getEl();
            var tabElRightEdge = tabElLeftEdge + tabEl.getWidthWithMargin();

            var userElRightEdge = Math.max(fullWidth, tabElRightEdge + userEl.getWidthWithMargin());
            userEl.setRight((fullWidth - userElRightEdge) + "px");
        }
    }

    export interface AppBarActionsConfig {

        showAppLauncherAction?:api.ui.Action;

        showAppBrowsePanelAction?:api.ui.Action;
    }

    export class LauncherButton extends api.dom.ButtonEl {

        constructor(action: api.ui.Action) {
            super('launcher-button');

            this.onClicked((event: MouseEvent) => {
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

        constructor(text: string, action: api.ui.Action) {
            super('home-button');

            this.getEl().setInnerHtml(text);

            this.onClicked((event: MouseEvent) => {
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

        setIcon(photoUrl: string) {
            this.getEl().setBackgroundImage('url("' + photoUrl + '")');
        }

    }

}