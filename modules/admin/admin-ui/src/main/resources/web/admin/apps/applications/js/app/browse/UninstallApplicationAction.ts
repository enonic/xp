module app.browse {

    import Application = api.application.Application;

    export class UninstallApplicationAction extends api.ui.Action {

        constructor(applicationTreeGrid: ApplicationTreeGrid) {
            super("Uninstall");
            this.setEnabled(false);

            this.onExecuted(() => {
                var applications: Application[] = applicationTreeGrid.getSelectedDataList();
                new UninstallApplicationDialog(applications).open();
            });
        }
    }
}
