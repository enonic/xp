module api.app.bar {

    import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;

    export class AppBar extends api.dom.DivEl implements api.ui.ActionContainer {

        protected application: Application;

        private appIcon: AppIcon;

        private showAppLauncherAction: ShowAppLauncherAction;

        constructor(application: Application) {
            super('appbar');

            this.application = application;

            this.showAppLauncherAction = new ShowAppLauncherAction(this.application);

            this.appIcon = new AppIcon(this.application);
            this.appendChild(this.appIcon);

            this.onRendered(() => {api.ui.responsive.ResponsiveManager.fireResizeEvent();});

        }

        getActions(): api.ui.Action[] {
            return [this.showAppLauncherAction];
        }

        setHomeIconAction() {
            this.appIcon.setAction(AppBarActions.SHOW_BROWSE_PANEL);
        }
    }

    export class AppIcon extends api.dom.DivEl {

        constructor(app: Application, action?: api.ui.Action) {

            super('home-button');

            if (app.getIconUrl()) {
                let icon = new api.dom.ImgEl(app.getIconUrl(), 'app-icon');
                this.appendChild(icon);
            }

            let span = new api.dom.SpanEl('app-name');
            span.setHtml(app.getName());
            this.appendChild(span);

            if (action) {
                this.setAction(action);
            }
        }

        setAction(action: api.ui.Action) {
            this.addClass('clickable');
            this.onClicked((event: MouseEvent) => {
                action.execute();
            });
        }

    }
}
