module api.app.bar {

    export class ShowAppLauncherAction extends api.ui.Action {

        constructor(application: api.app.Application) {
            super('Start', 'mod+esc', true);

            this.onExecuted(() => {
                new ShowAppLauncherEvent(application).fire(window.parent);
                new ShowAppLauncherEvent(application).fire();
            });
        }
    }
}