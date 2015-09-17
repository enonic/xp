module app.browse {

    import Application = api.application.Application;

    export class StopApplicationAction extends api.ui.Action {

        constructor(applicationTreeGrid: ApplicationTreeGrid) {
            super("Stop");
            this.setEnabled(false);
            this.onExecuted(() => {
                var applications: Application[] = applicationTreeGrid.getSelectedDataList();
                new StopApplicationEvent(applications).fire();
            });
        }
    }
}
