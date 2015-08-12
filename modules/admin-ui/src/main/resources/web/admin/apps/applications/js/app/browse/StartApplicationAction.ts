module app.browse {

    import Application = api.application.Application;

    export class StartApplicationAction extends api.ui.Action {

        constructor(applicationTreeGrid: ApplicationTreeGrid) {
            super("Start");
            this.setEnabled(false);
            this.onExecuted(() => {
                var applications: Application[] = applicationTreeGrid.getSelectedDataList();
                new StartApplicationEvent(applications).fire();
            });
        }
    }
}
