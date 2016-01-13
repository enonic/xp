module app.browse {

    import Application = api.application.Application;

    export class InstallApplicationAction extends api.ui.Action {

        constructor(applicationTreeGrid: ApplicationTreeGrid) {
            super("Install");
            this.setEnabled(false);
            this.onExecuted(() => {
                new app.installation.InstallAppPromptEvent().fire();
            });
        }
    }
}
