module app.browse {

    import Application = api.application.Application;

    export class InstallApplicationAction extends api.ui.Action {

        constructor(applicationTreeGrid: ApplicationTreeGrid) {
            super("Install");
            this.setEnabled(false);
            this.onExecuted(() => {
                const installedApplications: Application[] = applicationTreeGrid.getRoot().getCurrentRoot().treeToList().map(
                    (node) => node.getData());
                new app.installation.InstallAppPromptEvent(installedApplications).fire();
            });
        }
    }
}
