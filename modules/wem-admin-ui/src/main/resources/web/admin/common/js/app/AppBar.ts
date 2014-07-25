module api.app {

    export class AppBar extends api.dom.DivEl implements api.ui.ActionContainer {

        private application: Application;

        private launcherButton: api.dom.ButtonEl;

        private homeButton: api.dom.ButtonEl;

        private tabMenu: AppBarTabMenu;

        private userButton: UserButton;

        private userInfoPopup: UserInfoPopup;

        private showAppLauncherAction: ShowAppLauncherAction;

        constructor(application: Application) {
            super("appbar");

            this.application = application;
            this.tabMenu = new api.app.AppBarTabMenu();
            this.tabMenu.onNavigationItemSelected(() => this.layoutChildren());
            this.tabMenu.onNavigationItemDeselected(() => this.layoutChildren());
            this.tabMenu.onButtonLabelChanged(() => this.layoutChildren());

            this.showAppLauncherAction = new ShowAppLauncherAction(this.application);

            this.launcherButton = new LauncherButton(this.showAppLauncherAction);
            this.appendChild(this.launcherButton);

            this.homeButton = new HomeButton(this.application.getName(), AppBarActions.SHOW_BROWSE_PANEL);
            this.appendChild(this.homeButton);

            this.userButton = new UserButton();
            this.appendChild(this.userButton);

            this.appendChild(this.tabMenu);

            this.userInfoPopup = new UserInfoPopup();
            this.userInfoPopup.hide();

            this.userButton.onClicked((event: MouseEvent) => this.userInfoPopup.toggle());

            this.setBackgroundImgUrl(api.util.getRestUri('ui/background.jpg'));

            api.dom.Window.get().onResized((event: UIEvent) => this.layoutChildren(), this);
            this.onRendered((event) => this.layoutChildren());

            this.tabMenu.onNavigationItemAdded((event: api.ui.NavigatorEvent)=> {
                this.updateAppOpenTabs();
            });
            this.tabMenu.onNavigationItemRemoved((event: api.ui.NavigatorEvent)=> {
                this.updateAppOpenTabs();
            });
        }


        getActions(): api.ui.Action[] {
            return [this.showAppLauncherAction];
        }

        getTabMenu(): AppBarTabMenu {
            return this.tabMenu;
        }

        private updateAppOpenTabs() {
            this.application.setOpenTabs(this.tabMenu.countVisible());
        }

        private layoutChildren() {
            this.updateHomeButtonLabel();

            var fullWidth = this.getEl().getWidth();

            var homeEl = this.homeButton.getEl();
            var homeElRightEdge = homeEl.getOffset().left + homeEl.getWidthWithMargin();

            var userEl = this.userButton.getEl();
            var tabAvailableWidth = fullWidth - homeElRightEdge - userEl.getWidthWithMargin();

            var tabEl = this.tabMenu.getEl();
            tabEl.setWidth('auto').setWidth(tabEl.getWidthWithMargin() > tabAvailableWidth ? tabAvailableWidth + 'px' : 'auto');

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

    export class LauncherButton extends api.ui.button.ActionButton {

        constructor(action: api.ui.Action) {
            super(action, true);
            this.addClass('launcher-button');
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