module app.browse {

    import Application = api.module.Application;

    export class StopModuleAction extends api.ui.Action {

        constructor(moduleTreeGrid: ModuleTreeGrid) {
            super("Stop");
            this.setEnabled(false);
            this.onExecuted(() => {
                var applications: Application[] = moduleTreeGrid.getSelectedDataList();
                new StopModuleEvent(applications).fire();
            });
        }
    }
}
