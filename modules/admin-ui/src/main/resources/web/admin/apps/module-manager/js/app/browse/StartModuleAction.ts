module app.browse {

    import Module = api.module.Module;

    export class StartModuleAction extends api.ui.Action {

        constructor(moduleTreeGrid: ModuleTreeGrid) {
            super("Start");
            this.setEnabled(false);
            this.onExecuted(() => {
                var modules: Module[] = moduleTreeGrid.getSelectedDataList();
                new StartModuleEvent(modules).fire();
            });
        }
    }
}
