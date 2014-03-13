module api.app {

    export class AppBar extends api.dom.DivEl {

        private application: Application;

        private launcherButton: api.dom.ButtonEl;

        private homeButton: api.dom.ButtonEl;

        private tabMenu: AppBarTabMenu;

        private userButton: UserButton;

        private userInfoPopup: UserInfoPopup;

        constructor(application: Application) {
            super("appbar");

            this.application = application;
            this.tabMenu = new api.app.AppBarTabMenu();
            this.tabMenu.onNavigationItemSelected(() => this.layoutChildren());
            this.tabMenu.onNavigationItemDeselected(() => this.layoutChildren());
            this.tabMenu.onButtonLabelChanged(() => this.layoutChildren());

            this.launcherButton = new LauncherButton(AppBarActions.SHOW_APP_LAUNCHER);
            this.appendChild(this.launcherButton);

            var separator = new Separator();
            this.appendChild(separator);

            this.homeButton = new HomeButton(this.application.getName(), AppBarActions.SHOW_APP_BROWSE_PANEL);
            this.appendChild(this.homeButton);

            this.userButton = new UserButton();
            this.appendChild(this.userButton);

            this.appendChild(this.tabMenu);

            this.userInfoPopup = new UserInfoPopup();
            this.userInfoPopup.hide();

            this.userButton.onClicked((event: Event) => {
                this.userInfoPopup.toggle();
            });

            var appManager: api.app.AppManager = api.app.AppManager.instance();
            this.launcherButton.onClicked((event) => {
                appManager.showLauncher();
            });

            this.setBackgroundImgUrl(api.util.getRestUri('ui/background.jpg'));

            window.addEventListener('resize', () => this.layoutChildren());
            this.onRendered((event) => this.layoutChildren())
        }

        getTabMenu(): AppBarTabMenu {
            return this.tabMenu;
        }

        private layoutChildren() {
            this.updateHomeButtonLabel();

            var fullWidth = this.getEl().getWidth();

            var homeEl = this.homeButton.getEl();
            var homeElRightEdge = homeEl.getOffset().left + homeEl.getWidthWithMargin();

            var userEl = this.userButton.getEl();
            var tabAvailableWidth = fullWidth - homeElRightEdge - userEl.getWidthWithMargin();

            var tabEl = this.tabMenu.getEl();
            tabEl.setWidth('auto').setWidth(tabEl.getWidthWithMargin() > tabAvailableWidth ? tabAvailableWidth+'px' : 'auto');

            var centerLeftEdge = (fullWidth - tabEl.getWidth()) / 2;
            tabEl.setLeftPx(Math.max(homeElRightEdge, centerLeftEdge));

            if (this.tabMenu.isShowingMenuItems()) {
                this.tabMenu.updateMenuPosition();
            }
        }

        private updateHomeButtonLabel() {
            var fullWidth = this.getEl().getWidth(),
                homeEl = this.homeButton.getEl(),
                homeLabel = homeEl.getInnerHtml();

            if (fullWidth > 540 && homeLabel != this.application.getName()) {
                homeEl.setInnerHtml(this.application.getName());
            } else if (fullWidth <= 540 && homeLabel != this.application.getShortName()) {
                homeEl.setInnerHtml(this.application.getShortName());
            }
        }
    }

    export class LauncherButton extends api.dom.ButtonEl {

        constructor(action: api.ui.Action) {
            super('launcher-button');

            this.onClicked((event: Event) => {
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

            this.onClicked((event: Event) => {
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