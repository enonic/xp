module app.browse {

    import Application = api.module.Application;

    export class StartModuleAction extends api.ui.Action {

        constructor(moduleTreeGrid: ModuleTreeGrid) {
            super("Start");
            this.setEnabled(false);
            this.onExecuted(() => {
                var applications: Application[] = moduleTreeGrid.getSelectedDataList();
                new StartModuleEvent(applications).fire();
            });
        }
    }
}
