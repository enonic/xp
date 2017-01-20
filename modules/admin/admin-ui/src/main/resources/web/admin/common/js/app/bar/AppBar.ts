module api.app.bar {

    import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;

    export class AppBar extends api.dom.DivEl implements api.ui.ActionContainer {

        protected application: Application;

        private homeButton: HomeButton;

        private showAppLauncherAction: ShowAppLauncherAction;

        constructor(application: Application) {
            super('appbar');

            this.application = application;

            this.showAppLauncherAction = new ShowAppLauncherAction(this.application);

            this.homeButton = new HomeButton(this.application, AppBarActions.SHOW_BROWSE_PANEL);
            this.appendChild(this.homeButton);

            this.onRendered(() => {api.ui.responsive.ResponsiveManager.fireResizeEvent();});

        }

        getActions(): api.ui.Action[] {
            return [this.showAppLauncherAction];
        }
    }

    export class HomeButton extends api.ui.button.Button {

        constructor(app: Application, action: api.ui.Action) {

            super(app.getName());

            this.addClass('home-button app-icon icon-' + app.getIconUrl());

            this.onClicked((event: MouseEvent) => {
                action.execute();
            });
        }

    }
}
