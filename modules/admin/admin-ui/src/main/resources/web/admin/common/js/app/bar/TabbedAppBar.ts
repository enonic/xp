module api.app.bar {

    import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;

    export class TabbedAppBar extends AppBar implements api.ui.ActionContainer {

        private tabMenu: AppBarTabMenu;

        constructor(application: Application) {
            super(application);

            this.tabMenu = new AppBarTabMenu();

            this.appendChild(this.tabMenu);

            this.tabMenu.onNavigationItemAdded((event: api.ui.NavigatorEvent) => {
                this.updateAppOpenTabs();
            });
            this.tabMenu.onNavigationItemRemoved((event: api.ui.NavigatorEvent) => {
                this.updateAppOpenTabs();
            });

            // Responsive events to update homeButton styles
            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, (item: api.ui.responsive.ResponsiveItem) => {
                if (this.tabMenu.countVisible() > 0) {
                    this.addClass("tabs-present");
                } else {
                    this.removeClass("tabs-present");
                }
            });
        }

        private updateAppOpenTabs() {
            this.application.setOpenTabs(this.tabMenu.countVisible());
        }

        getTabMenu(): AppBarTabMenu {
            return this.tabMenu;
        }
    }
}