module api.app.bar {

    export class AppBar extends api.dom.DivEl implements api.ui.ActionContainer {

        private application: Application;

        private launcherButton: api.dom.ButtonEl;

        private homeButton: HomeButton;

        private tabMenu: AppBarTabMenu;

        private showAppLauncherAction: ShowAppLauncherAction;

        constructor(application: Application) {
            super("appbar");

            this.application = application;
            this.tabMenu = new AppBarTabMenu();

            this.showAppLauncherAction = new ShowAppLauncherAction(this.application);

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

            // Responsive events to update homeButton styles
            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this);
            this.onRendered(() => {api.ui.responsive.ResponsiveManager.fireResizeEvent();});

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