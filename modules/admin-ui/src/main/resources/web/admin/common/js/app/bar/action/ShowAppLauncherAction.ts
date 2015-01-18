module api.app.bar.action {

    export class ShowAppLauncherAction extends api.ui.Action {

        constructor(application: api.app.Application) {
            super('Start', 'esc', true);

            this.onExecuted(() => {
                new api.app.bar.event.ShowAppLauncherEvent(application).fire(window.parent);
                new api.app.bar.event.ShowAppLauncherEvent(application).fire();
            });
        }
    }
}