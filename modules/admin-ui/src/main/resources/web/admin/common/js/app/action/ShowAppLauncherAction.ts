module api.app.action {

    export class ShowAppLauncherAction extends api.ui.Action {

        constructor(application: api.app.Application) {
            super('Start', 'esc', true);

            this.onExecuted(() => {
                new ShowAppLauncherEvent(application).fire(window.parent);
                new ShowAppLauncherEvent(application).fire();
            });
        }
    }
}