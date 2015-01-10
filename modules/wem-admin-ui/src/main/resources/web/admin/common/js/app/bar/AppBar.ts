module api.app.bar {

    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;

    import AppBarActions = api.app.bar.action.AppBarActions;

    export class AppBar extends api.dom.DivEl implements api.ui.ActionContainer {

        private application: Application;

        private launcherButton: api.dom.ButtonEl;

        private homeButton: HomeButton;

        private appImageButton: api.dom.ButtonEl;

        private tabMenu: AppBarTabMenu;

        private showAppLauncherAction: api.app.bar.action.ShowAppLauncherAction;

        constructor(application: Application) {
            super("appbar");

            this.application = application;
            this.tabMenu = new AppBarTabMenu();
            this.tabMenu.onNavigationItemSelected(() => this.layoutChildren());
            this.tabMenu.onNavigationItemDeselected(() => this.layoutChildren());
            this.tabMenu.onButtonLabelChanged(() => this.layoutChildren());

            this.showAppLauncherAction = new action.ShowAppLauncherAction(this.application);

            this.launcherButton = new LauncherButton(this.showAppLauncherAction);
            this.appendChild(this.launcherButton);

            this.homeButton = new HomeButton(this.application, AppBarActions.SHOW_BROWSE_PANEL);
            this.appendChild(this.homeButton);

            this.appendChild(this.tabMenu);

            this.tabMenu.onNavigationItemAdded((event: api.ui.NavigatorEvent)=> {
                this.updateAppOpenTabs();
            });
            this.tabMenu.onNavigationItemRemoved((event: api.ui.NavigatorEvent)=> {
                this.updateAppOpenTabs();
            });

            ResponsiveManager.onAvailableSizeChanged(this, this.layoutChildren.bind(this));
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

        // TODO: Remove, when the new tab implementation is ready.
        private layoutChildren() {
            var fullWidth = this.getEl().getWidth();

            var homeEl = this.homeButton.getEl();
            var homeElRightEdge = homeEl.getOffset().left + homeEl.getWidthWithMargin();

            var tabAvailableWidth = fullWidth - homeElRightEdge;

            var tabEl = this.tabMenu.getEl();
            tabEl.setWidth('auto').setWidth(tabEl.getWidthWithMargin() > tabAvailableWidth ? tabAvailableWidth + 'px' : 'auto');

            var centerLeftEdge = (fullWidth - tabEl.getWidth()) / 2;
            tabEl.setLeftPx(Math.max(homeElRightEdge, centerLeftEdge));

            if (this.tabMenu.isMenuVisible()) {
                this.tabMenu.updateMenuPosition();
            }
        }
    }

    export class LauncherButton extends api.ui.button.ActionButton {

        constructor(action: api.ui.Action) {
            super(action, false);
            this.addClass('launcher-button');
        }

    }

    export class HomeButton extends api.ui.button.Button {

        constructor(app: Application, action: api.ui.Action) {

            super(app.getName());

            this.addClass("home-button app-icon icon-" + app.getIconUrl());

            this.onClicked((event: MouseEvent) => {
                action.execute();
            });
        }

    }
}